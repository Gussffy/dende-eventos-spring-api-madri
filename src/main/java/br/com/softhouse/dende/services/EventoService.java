package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.request.EventoRequestDTO;
import br.com.softhouse.dende.dto.response.EventoResponseDTO;
import br.com.softhouse.dende.dto.response.EventoResumoDTO;
import br.com.softhouse.dende.exceptions.ConflictException;
import br.com.softhouse.dende.exceptions.NotFoundException;
import br.com.softhouse.dende.exceptions.ValidationException;
import br.com.softhouse.dende.mappers.EventoMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.builders.EventoBuilder;
import br.com.softhouse.dende.model.enums.StatusIngresso;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.IngressoRepository;
import br.com.softhouse.dende.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final IngressoRepository ingressoRepository;

    @Transactional
    public EventoResponseDTO cadastrar(Long organizadorId, EventoRequestDTO dto) {
        Usuario org = usuarioRepository.findByIdAndTipoUsuario(organizadorId, "ORGANIZADOR")
                .orElseThrow(() -> new NotFoundException("Organizador não encontrado"));
        if (!org.getAtivo()) throw new ConflictException("Organizador inativo não pode cadastrar eventos");

        if (dto.getEventoPrincipalId() != null) {
            Evento principal = eventoRepository.findById(dto.getEventoPrincipalId())
                    .orElseThrow(() -> new NotFoundException("Evento principal não encontrado"));
            if (!principal.getOrganizadorId().equals(organizadorId)) {
                throw new ConflictException("Evento principal não pertence a este organizador");
            }
        }

        Evento evento = EventoBuilder.builder()
                .organizadorId(organizadorId)
                .nome(dto.getNome())
                .pagina(dto.getPagina())
                .descricao(dto.getDescricao())
                .dataInicio(dto.getDataInicio())
                .dataFinal(dto.getDataFinal())
                .tipoEvento(dto.getTipoEvento())
                .eventoPrincipalId(dto.getEventoPrincipalId())
                .modalidade(dto.getModalidade())
                .capacidadeMaxima(dto.getCapacidadeMaxima())
                .local(dto.getLocal())
                .precoIngresso(dto.getPrecoIngresso())
                .estornaCancelamento(dto.getEstornaCancelamento() != null ? dto.getEstornaCancelamento() : true)
                .taxaEstorno(dto.getTaxaEstorno() != null ? dto.getTaxaEstorno() : 0.0)
                .ativo(false)
                .build();

        if (!evento.validarDatas()) {
            throw new ValidationException("Datas inválidas: verifique se a data de início é futura, " +
                    "data de fim é posterior à data de início e duração mínima de 30 minutos");
        }

        evento = eventoRepository.save(evento);
        carregarIngressosVendidos(evento);
        return EventoMapper.toDTO(evento);
    }

    @Transactional(readOnly = true)
    public EventoResponseDTO buscarPorId(Long id) {
        Evento evento = buscarEntidadePorId(id);
        return EventoMapper.toDTO(evento);
    }

    @Transactional(readOnly = true)
    public Evento buscarEntidadePorId(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));
        carregarIngressosVendidos(evento);
        return evento;
    }

    @Transactional
    public EventoResponseDTO atualizar(Long organizadorId, Long eventoId, EventoRequestDTO dto) {
        Evento existente = buscarEntidadePorId(eventoId);

        if (!existente.getOrganizadorId().equals(organizadorId)) {
            throw new ConflictException("Este evento não pertence ao organizador");
        }
        if (!existente.getAtivo()) {
            throw new ConflictException("Não é possível alterar um evento inativo");
        }

        if (dto.getEventoPrincipalId() != null && !dto.getEventoPrincipalId().equals(existente.getEventoPrincipalId())) {
            Evento principal = eventoRepository.findById(dto.getEventoPrincipalId())
                    .orElseThrow(() -> new NotFoundException("Evento principal não encontrado"));
            if (!principal.getOrganizadorId().equals(organizadorId)) {
                throw new ConflictException("Evento principal não pertence a este organizador");
            }
        }

        Evento atualizado = EventoMapper.updateEntity(existente, dto);
        if (!atualizado.validarDatas()) {
            throw new ValidationException("Datas inválidas após alteração");
        }

        eventoRepository.save(atualizado);
        return EventoMapper.toDTO(atualizado);
    }

    @Transactional
    public EventoResponseDTO ativar(Long organizadorId, Long eventoId) {
        Evento evento = buscarEntidadePorId(eventoId);

        if (!evento.getOrganizadorId().equals(organizadorId)) throw new ConflictException("Este evento não pertence ao organizador");
        if (evento.getAtivo()) throw new ConflictException("Evento já está ativo");
        if (!evento.podeSerAtivado()) throw new ConflictException("Evento não pode ser ativado: verifique as datas");

        evento.setAtivo(true);
        eventoRepository.save(evento);
        return EventoMapper.toDTO(evento);
    }

    @Transactional
    public EventoResponseDTO desativar(Long organizadorId, Long eventoId) {
        Evento evento = buscarEntidadePorId(eventoId);

        if (!evento.getOrganizadorId().equals(organizadorId)) throw new ConflictException("Este evento não pertence ao organizador");
        if (!evento.getAtivo()) throw new ConflictException("Evento já está inativo");

        if (evento.getIngressosVendidos() > 0) {
            List<Ingresso> ingressos = ingressoRepository.findByEventoIdOrderByDataCompraDesc(eventoId);
            for (Ingresso ingresso : ingressos) {
                if (ingresso.getStatus() == StatusIngresso.ATIVO) {
                    ingresso.reembolsar();
                    ingressoRepository.save(ingresso);
                }
            }
            evento.setIngressosVendidos(0);
        }

        evento.setAtivo(false);
        eventoRepository.save(evento);
        return EventoMapper.toDTO(evento);
    }

    @Transactional(readOnly = true)
    public List<EventoResumoDTO> listarPorOrganizador(Long organizadorId) {
        return eventoRepository.findByOrganizadorIdOrderById(organizadorId).stream()
                .peek(this::carregarIngressosVendidos)
                .map(EventoMapper::toResumoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventoResponseDTO> feedAtivos() {
        List<Evento> eventos = eventoRepository.findAtivos(LocalDateTime.now());
        eventos.forEach(this::carregarIngressosVendidos);
        return eventos.stream()
                .filter(Evento::temIngressosDisponiveis)
                .sorted((e1, e2) -> {
                    int cmp = e1.getDataInicio().compareTo(e2.getDataInicio());
                    return cmp != 0 ? cmp : e1.getNome().compareTo(e2.getNome());
                })
                .map(EventoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean organizadorTemEventosAtivos(Long organizadorId) {
        return eventoRepository.existsAtivosOuEmExecucao(organizadorId, LocalDateTime.now());
    }

    private void carregarIngressosVendidos(Evento evento) {
        if (evento != null) {
            evento.setIngressosVendidos(ingressoRepository.countIngressosValidosPorEvento(evento.getId()));
        }
    }
}

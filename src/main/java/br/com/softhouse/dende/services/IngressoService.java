package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.request.CompraRequestDTO;
import br.com.softhouse.dende.dto.response.CancelamentoResponseDTO;
import br.com.softhouse.dende.dto.response.CompraResponseDTO;
import br.com.softhouse.dende.dto.response.IngressoResponseDTO;
import br.com.softhouse.dende.exceptions.ConflictException;
import br.com.softhouse.dende.exceptions.NotFoundException;
import br.com.softhouse.dende.exceptions.ValidationException;
import br.com.softhouse.dende.mappers.IngressoMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.enums.StatusIngresso;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.IngressoRepository;
import br.com.softhouse.dende.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public CompraResponseDTO comprar(Long organizadorId, Long eventoId, CompraRequestDTO request) {
        // Validar usuário
        Usuario usuario = usuarioRepository.findByEmailAndTipoUsuario(request.getUsuarioEmail(), "COMUM")
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        if (!usuario.getAtivo()) throw new ConflictException("Usuário inativo não pode comprar ingressos");

        // Validar evento
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));
        evento.setIngressosVendidos(ingressoRepository.countIngressosValidosPorEvento(eventoId));

        if (!evento.getOrganizadorId().equals(organizadorId)) throw new ConflictException("Evento não pertence a este organizador");
        if (!evento.getAtivo()) throw new ConflictException("Evento inativo não está vendendo ingressos");
        if (evento.eventoJaAconteceu()) throw new ConflictException("Evento já aconteceu");
        if (!evento.temIngressosDisponiveis()) throw new ConflictException("Ingressos esgotados");
        if (ingressoRepository.existsByUsuarioIdAndEventoIdAndStatus(usuario.getId(), eventoId, StatusIngresso.ATIVO)) {
            throw new ConflictException("Você já possui um ingresso ativo para este evento");
        }

        // Calcular valor esperado e verificar evento principal
        double valorEsperado = evento.getPrecoIngresso();
        Evento principal = null;

        if (evento.getEventoPrincipalId() != null) {
            principal = eventoRepository.findById(evento.getEventoPrincipalId()).orElse(null);
            if (principal != null && principal.getAtivo()) {
                principal.setIngressosVendidos(ingressoRepository.countIngressosValidosPorEvento(principal.getId()));
                if (!principal.temIngressosDisponiveis()) {
                    throw new ConflictException("Evento principal não tem ingressos disponíveis");
                }
                valorEsperado += principal.getPrecoIngresso();
            }
        }

        // Validar valor pago
        if (request.getTotalPago() != null && Math.abs(request.getTotalPago() - valorEsperado) > 0.01) {
            throw new ValidationException(String.format(
                    "Valor pago (R$ %.2f) não corresponde ao preço do ingresso (R$ %.2f)",
                    request.getTotalPago(), valorEsperado));
        }

        double valorTotal = evento.getPrecoIngresso();
        List<String> codigos = new ArrayList<>();

        // Criar ingresso principal
        Ingresso ingressoPrincipal = IngressoMapper.createIngresso(usuario.getId(), eventoId, evento.getPrecoIngresso());
        ingressoRepository.save(ingressoPrincipal);
        evento.venderIngresso();
        eventoRepository.save(evento);
        codigos.add(ingressoPrincipal.getCodigo());

        // Se for evento vinculado, criar ingresso para o evento principal
        if (principal != null && principal.getAtivo() && principal.temIngressosDisponiveis()) {
            Ingresso ingressoVinculado = IngressoMapper.createIngressoVinculado(
                    usuario.getId(), principal.getId(), principal.getPrecoIngresso());
            ingressoRepository.save(ingressoVinculado);
            principal.venderIngresso();
            eventoRepository.save(principal);
            valorTotal += principal.getPrecoIngresso();
            codigos.add(ingressoVinculado.getCodigo());
        }

        String mensagem = codigos.size() > 1
                ? "Compra realizada com sucesso! Ingressos gerados: " + String.join(", ", codigos)
                : "Compra realizada com sucesso! Ingresso gerado: " + codigos.get(0);

        return new CompraResponseDTO(mensagem, codigos, valorTotal, "AGUARDANDO_PAGAMENTO");
    }

    @Transactional
    public CancelamentoResponseDTO cancelar(Long usuarioId, Long ingressoId) {
        usuarioRepository.findByIdAndTipoUsuario(usuarioId, "COMUM")
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        Ingresso ingresso = ingressoRepository.findById(ingressoId)
                .orElseThrow(() -> new NotFoundException("Ingresso não encontrado"));

        if (!ingresso.getUsuarioId().equals(usuarioId)) throw new ConflictException("Ingresso não pertence a este usuário");
        if (!ingresso.podeSerCancelado()) throw new ConflictException("Este ingresso não pode ser cancelado");

        Evento evento = eventoRepository.findById(ingresso.getEventoId())
                .orElseThrow(() -> new NotFoundException("Evento associado ao ingresso não encontrado"));
        evento.setIngressosVendidos(ingressoRepository.countIngressosValidosPorEvento(evento.getId()));

        if (evento.eventoJaAconteceu()) throw new ConflictException("Não é possível cancelar ingresso de evento já realizado");

        double valorReembolso = evento.calcularReembolso(ingresso.getValorPago());
        ingresso.cancelar();
        ingresso.setValorEstornado(valorReembolso);
        ingresso.setDataCancelamento(LocalDateTime.now());
        ingressoRepository.save(ingresso);

        evento.cancelarIngresso();
        eventoRepository.save(evento);

        String mensagem;
        if (evento.getEstornaCancelamento()) {
            mensagem = String.format(
                    "Ingresso cancelado. Código: %s, Valor pago: R$ %.2f, Reembolso: R$ %.2f (taxa de %.1f%%)",
                    ingresso.getCodigo(), ingresso.getValorPago(), valorReembolso, evento.getTaxaEstorno());
        } else {
            mensagem = String.format(
                    "Ingresso cancelado. Código: %s, Valor pago: R$ %.2f, Este evento não oferece reembolso.",
                    ingresso.getCodigo(), ingresso.getValorPago());
        }

        return new CancelamentoResponseDTO(mensagem, ingresso.getValorPago(), valorReembolso, ingresso.getCodigo());
    }

    @Transactional(readOnly = true)
    public List<IngressoResponseDTO> listarPorUsuario(Long usuarioId) {
        usuarioRepository.findByIdAndTipoUsuario(usuarioId, "COMUM")
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        List<Ingresso> ingressos = ingressoRepository.findByUsuarioIdOrderByDataCompraDesc(usuarioId);

        ingressos.sort((i1, i2) -> {
            Evento e1 = eventoRepository.findById(i1.getEventoId()).orElse(null);
            Evento e2 = eventoRepository.findById(i2.getEventoId()).orElse(null);
            if (e1 == null || e2 == null) return 0;

            boolean i1Ativo = i1.getStatus() == StatusIngresso.ATIVO && !e1.eventoJaAconteceu();
            boolean i2Ativo = i2.getStatus() == StatusIngresso.ATIVO && !e2.eventoJaAconteceu();

            if (i1Ativo && !i2Ativo) return -1;
            if (!i1Ativo && i2Ativo) return 1;
            return e1.getDataInicio().compareTo(e2.getDataInicio());
        });

        return ingressos.stream()
                .map(i -> {
                    Evento e = eventoRepository.findById(i.getEventoId()).orElse(null);
                    return IngressoMapper.toDTO(i, e);
                })
                .collect(Collectors.toList());
    }
}

package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.request.EmpresaRequestDTO;
import br.com.softhouse.dende.dto.request.OrganizadorRequestDTO;
import br.com.softhouse.dende.dto.response.EmpresaResponseDTO;
import br.com.softhouse.dende.dto.response.OrganizadorResponseDTO;
import br.com.softhouse.dende.exceptions.ConflictException;
import br.com.softhouse.dende.exceptions.NotFoundException;
import br.com.softhouse.dende.exceptions.UnauthorizedException;
import br.com.softhouse.dende.exceptions.ValidationException;
import br.com.softhouse.dende.mappers.EmpresaMapper;
import br.com.softhouse.dende.mappers.OrganizadorMapper;
import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrganizadorService {

    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final EmpresaService empresaService;

    @Transactional
    public OrganizadorResponseDTO cadastrar(OrganizadorRequestDTO dto) {
        if (dto == null) throw new ValidationException("Dados do organizador são obrigatórios");
        if (dto.getNome() == null || dto.getNome().isBlank()) throw new ValidationException("Nome é obrigatório");
        if (dto.getDataNascimento() == null) throw new ValidationException("Data de nascimento é obrigatória");
        if (dto.getSexo() == null) throw new ValidationException("Sexo é obrigatório");
        if (dto.getEmail() == null || dto.getEmail().isBlank()) throw new ValidationException("Email é obrigatório");
        if (dto.getSenha() == null || dto.getSenha().isBlank()) throw new ValidationException("Senha é obrigatória");

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email já está em uso");
        }

        validarDadosEmpresaOpcional(dto);

        Usuario organizador = OrganizadorMapper.toEntity(dto);
        organizador = usuarioRepository.save(organizador);

        Empresa empresa = null;
        if (temDadosEmpresa(dto)) {
            EmpresaRequestDTO empresaDTO = OrganizadorMapper.toEmpresaDTO(dto, organizador.getId());
            EmpresaResponseDTO empresaCadastrada = empresaService.cadastrar(empresaDTO);
            empresa = EmpresaMapper.toEntity(empresaCadastrada);
        }

        return OrganizadorMapper.toDTO(organizador, empresa);
    }

    @Transactional(readOnly = true)
    public OrganizadorResponseDTO buscarPorId(Long id) {
        Usuario org = buscarEntidadePorId(id);
        Empresa empresa = empresaService.buscarEntidadePorOrganizadorId(id);
        return OrganizadorMapper.toDTO(org, empresa);
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findByIdAndTipoUsuario(id, "ORGANIZADOR")
                .orElseThrow(() -> new NotFoundException("Organizador não encontrado"));
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorEmail(String email) {
        return usuarioRepository.findByEmailAndTipoUsuario(email, "ORGANIZADOR")
                .orElseThrow(() -> new NotFoundException("Organizador não encontrado"));
    }

    @Transactional
    public OrganizadorResponseDTO atualizar(Long id, OrganizadorRequestDTO dto) {
        if (id == null) throw new ValidationException("ID do organizador é obrigatório");
        if (dto == null) throw new ValidationException("Dados do organizador são obrigatórios");

        Usuario existente = buscarEntidadePorId(id);
        if (dto.getEmail() != null && !dto.getEmail().equals(existente.getEmail())) {
            throw new ConflictException("Não é permitido alterar o email");
        }

        Usuario atualizado = OrganizadorMapper.updateEntity(existente, dto);
        usuarioRepository.save(atualizado);

        Empresa empresa = empresaService.buscarEntidadePorOrganizadorId(id);
        return OrganizadorMapper.toDTO(atualizado, empresa);
    }

    @Transactional
    public OrganizadorResponseDTO ativarComSenha(Long id, String senha) {
        Usuario organizador = buscarEntidadePorId(id);
        if (!organizador.getSenha().equals(senha)) throw new UnauthorizedException("Senha incorreta");
        if (organizador.getAtivo()) throw new ConflictException("Organizador já está ativo");
        organizador.setAtivo(true);
        usuarioRepository.save(organizador);
        Empresa empresa = empresaService.buscarEntidadePorOrganizadorId(id);
        return OrganizadorMapper.toDTO(organizador, empresa);
    }

    @Transactional
    public OrganizadorResponseDTO desativarComSenha(Long id, String senha) {
        Usuario organizador = buscarEntidadePorId(id);
        if (!organizador.getSenha().equals(senha)) throw new UnauthorizedException("Senha incorreta");
        if (!organizador.getAtivo()) throw new ConflictException("Organizador já está inativo");
        if (eventoRepository.existsAtivosOuEmExecucao(id, LocalDateTime.now())) {
            throw new ConflictException("Não é possível desativar: organizador possui eventos ativos ou em execução");
        }
        organizador.setAtivo(false);
        usuarioRepository.save(organizador);
        Empresa empresa = empresaService.buscarEntidadePorOrganizadorId(id);
        return OrganizadorMapper.toDTO(organizador, empresa);
    }

    private void validarDadosEmpresaOpcional(OrganizadorRequestDTO dto) {
        boolean temAlgum = temAlgumDadoEmpresa(dto);
        if (!temAlgum) return;
        if (dto.getCnpj() == null || dto.getCnpj().isBlank()) throw new ValidationException("CNPJ é obrigatório quando a empresa for informada");
        if (dto.getRazaoSocial() == null || dto.getRazaoSocial().isBlank()) throw new ValidationException("Razão social é obrigatória quando a empresa for informada");
        if (dto.getNomeFantasia() == null || dto.getNomeFantasia().isBlank()) throw new ValidationException("Nome fantasia é obrigatório quando a empresa for informada");
    }

    private boolean temDadosEmpresa(OrganizadorRequestDTO dto) {
        return dto.getCnpj() != null && !dto.getCnpj().isBlank();
    }

    private boolean temAlgumDadoEmpresa(OrganizadorRequestDTO dto) {
        return (dto.getCnpj() != null && !dto.getCnpj().isBlank())
                || (dto.getRazaoSocial() != null && !dto.getRazaoSocial().isBlank())
                || (dto.getNomeFantasia() != null && !dto.getNomeFantasia().isBlank());
    }
}

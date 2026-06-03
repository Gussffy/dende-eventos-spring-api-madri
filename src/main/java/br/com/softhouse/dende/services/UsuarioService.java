package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.request.UsuarioRequestDTO;
import br.com.softhouse.dende.dto.response.UsuarioResponseDTO;
import br.com.softhouse.dende.exceptions.ConflictException;
import br.com.softhouse.dende.exceptions.NotFoundException;
import br.com.softhouse.dende.exceptions.UnauthorizedException;
import br.com.softhouse.dende.exceptions.ValidationException;
import br.com.softhouse.dende.mappers.UsuarioMapper;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) throw new ValidationException("Nome é obrigatório");
        if (dto.getDataNascimento() == null) throw new ValidationException("Data de nascimento é obrigatória");
        if (dto.getSexo() == null) throw new ValidationException("Sexo é obrigatório");
        if (dto.getEmail() == null || dto.getEmail().isBlank()) throw new ValidationException("Email é obrigatório");
        if (dto.getSenha() == null || dto.getSenha().isBlank()) throw new ValidationException("Senha é obrigatória");

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email já está em uso");
        }

        Usuario usuario = UsuarioMapper.toEntity(dto);
        usuario = usuarioRepository.save(usuario);
        return UsuarioMapper.toDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = buscarEntidadePorId(id);
        return UsuarioMapper.toDTO(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findByIdAndTipoUsuario(id, "COMUM")
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorEmail(String email) {
        return usuarioRepository.findByEmailAndTipoUsuario(email, "COMUM")
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        if (id == null) throw new ValidationException("ID do usuário é obrigatório");
        Usuario existente = buscarEntidadePorId(id);
        if (dto.getEmail() != null && !dto.getEmail().equals(existente.getEmail())) {
            throw new ConflictException("Não é permitido alterar o email");
        }
        Usuario atualizado = UsuarioMapper.updateEntity(existente, dto);
        usuarioRepository.save(atualizado);
        return UsuarioMapper.toDTO(atualizado);
    }

    @Transactional
    public UsuarioResponseDTO ativarComSenha(Long id, String senha) {
        Usuario usuario = buscarEntidadePorId(id);
        if (!usuario.getSenha().equals(senha)) throw new UnauthorizedException("Senha incorreta");
        if (usuario.getAtivo()) throw new ConflictException("Usuário já está ativo");
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
        return UsuarioMapper.toDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO desativarComSenha(Long id, String senha) {
        Usuario usuario = buscarEntidadePorId(id);
        if (!usuario.getSenha().equals(senha)) throw new UnauthorizedException("Senha incorreta");
        if (!usuario.getAtivo()) throw new ConflictException("Usuário já está inativo");
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        return UsuarioMapper.toDTO(usuario);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}

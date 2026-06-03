package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.dto.request.UsuarioRequestDTO;
import br.com.softhouse.dende.dto.response.UsuarioResponseDTO;
import br.com.softhouse.dende.exceptions.ConflictException;
import br.com.softhouse.dende.model.Usuario;

public class UsuarioMapper {

    private UsuarioMapper() {}

    public static Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto == null) return null;
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setSexo(dto.getSexo());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setTipoUsuario("COMUM");
        usuario.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        return usuario;
    }

    public static UsuarioResponseDTO toDTO(Usuario usuario) {
        if (usuario == null) return null;
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setDataNascimento(usuario.getDataNascimento());
        dto.setIdade(usuario.getIdade());
        dto.setSexo(usuario.getSexo());
        dto.setEmail(usuario.getEmail());
        dto.setAtivo(usuario.getAtivo());
        return dto;
    }

    public static Usuario updateEntity(Usuario usuario, UsuarioRequestDTO dto) {
        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            throw new ConflictException("Não é permitido alterar o email");
        }
        if (dto.getNome() != null) usuario.setNome(dto.getNome());
        if (dto.getDataNascimento() != null) usuario.setDataNascimento(dto.getDataNascimento());
        if (dto.getSexo() != null) usuario.setSexo(dto.getSexo());
        if (dto.getSenha() != null) usuario.setSenha(dto.getSenha());
        if (dto.getAtivo() != null) usuario.setAtivo(dto.getAtivo());
        return usuario;
    }
}

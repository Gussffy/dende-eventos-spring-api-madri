package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.dto.request.EmpresaRequestDTO;
import br.com.softhouse.dende.dto.request.OrganizadorRequestDTO;
import br.com.softhouse.dende.dto.response.OrganizadorResponseDTO;
import br.com.softhouse.dende.exceptions.ConflictException;
import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Usuario;

public class OrganizadorMapper {

    private OrganizadorMapper() {}

    public static Usuario toEntity(OrganizadorRequestDTO dto) {
        if (dto == null) return null;
        Usuario organizador = new Usuario();
        organizador.setNome(dto.getNome());
        organizador.setDataNascimento(dto.getDataNascimento());
        organizador.setSexo(dto.getSexo());
        organizador.setEmail(dto.getEmail());
        organizador.setSenha(dto.getSenha());
        organizador.setTipoUsuario("ORGANIZADOR");
        organizador.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        return organizador;
    }

    public static OrganizadorResponseDTO toDTO(Usuario organizador, Empresa empresa) {
        if (organizador == null) return null;
        OrganizadorResponseDTO dto = new OrganizadorResponseDTO();
        dto.setId(organizador.getId());
        dto.setNome(organizador.getNome());
        dto.setDataNascimento(organizador.getDataNascimento());
        dto.setIdade(organizador.getIdade());
        dto.setSexo(organizador.getSexo() != null ? organizador.getSexo().getDescricao() : null);
        dto.setEmail(organizador.getEmail());
        dto.setAtivo(organizador.getAtivo());
        if (empresa != null) {
            dto.setCnpj(empresa.getCnpj());
            dto.setRazaoSocial(empresa.getRazaoSocial());
            dto.setNomeFantasia(empresa.getNomeFantasia());
        }
        return dto;
    }

    public static Usuario updateEntity(Usuario organizador, OrganizadorRequestDTO dto) {
        if (dto == null) return organizador;
        if (dto.getEmail() != null && !dto.getEmail().equals(organizador.getEmail())) {
            throw new ConflictException("Não é permitido alterar o email");
        }
        if (dto.getNome() != null) organizador.setNome(dto.getNome());
        if (dto.getDataNascimento() != null) organizador.setDataNascimento(dto.getDataNascimento());
        if (dto.getSexo() != null) organizador.setSexo(dto.getSexo());
        if (dto.getSenha() != null) organizador.setSenha(dto.getSenha());
        if (dto.getAtivo() != null) organizador.setAtivo(dto.getAtivo());
        return organizador;
    }

    public static EmpresaRequestDTO toEmpresaDTO(OrganizadorRequestDTO dto, Long organizadorId) {
        if (dto == null) return null;
        boolean semDados = (dto.getCnpj() == null || dto.getCnpj().isBlank())
                && (dto.getRazaoSocial() == null || dto.getRazaoSocial().isBlank())
                && (dto.getNomeFantasia() == null || dto.getNomeFantasia().isBlank());
        if (semDados) return null;
        return new EmpresaRequestDTO(organizadorId, dto.getCnpj(), dto.getRazaoSocial(), dto.getNomeFantasia());
    }
}

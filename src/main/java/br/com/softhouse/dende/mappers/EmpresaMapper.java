package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.dto.request.EmpresaRequestDTO;
import br.com.softhouse.dende.dto.response.EmpresaResponseDTO;
import br.com.softhouse.dende.model.Empresa;

public final class EmpresaMapper {

    private EmpresaMapper() {}

    public static EmpresaResponseDTO toDTO(Empresa empresa) {
        if (empresa == null) return null;
        return new EmpresaResponseDTO(
                empresa.getId(), empresa.getOrganizadorId(),
                empresa.getCnpj(), empresa.getRazaoSocial(), empresa.getNomeFantasia());
    }

    public static Empresa toEntity(EmpresaRequestDTO dto) {
        if (dto == null) return null;
        return new Empresa(dto.getId(), dto.getOrganizadorId(),
                dto.getCnpj(), dto.getRazaoSocial(), dto.getNomeFantasia());
    }

    public static Empresa toEntity(EmpresaResponseDTO dto) {
        if (dto == null) return null;
        return new Empresa(dto.getId(), dto.getOrganizadorId(),
                dto.getCnpj(), dto.getRazaoSocial(), dto.getNomeFantasia());
    }

    public static Empresa updateEntity(Empresa entity, EmpresaRequestDTO dto) {
        if (entity == null || dto == null) return entity;
        if (dto.getOrganizadorId() != null) entity.setOrganizadorId(dto.getOrganizadorId());
        if (dto.getCnpj() != null && !dto.getCnpj().isBlank()) entity.setCnpj(dto.getCnpj());
        if (dto.getRazaoSocial() != null && !dto.getRazaoSocial().isBlank()) entity.setRazaoSocial(dto.getRazaoSocial());
        if (dto.getNomeFantasia() != null && !dto.getNomeFantasia().isBlank()) entity.setNomeFantasia(dto.getNomeFantasia());
        return entity;
    }
}

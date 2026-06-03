package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.request.EmpresaRequestDTO;
import br.com.softhouse.dende.dto.response.EmpresaResponseDTO;
import br.com.softhouse.dende.exceptions.ConflictException;
import br.com.softhouse.dende.exceptions.ValidationException;
import br.com.softhouse.dende.mappers.EmpresaMapper;
import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.repositories.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional
    public EmpresaResponseDTO cadastrar(EmpresaRequestDTO dto) {
        if (dto.getOrganizadorId() == null) throw new ValidationException("ID do organizador é obrigatório");
        if (dto.getCnpj() == null || dto.getCnpj().isBlank()) throw new ValidationException("CNPJ é obrigatório");
        if (dto.getRazaoSocial() == null || dto.getRazaoSocial().isBlank()) throw new ValidationException("Razão social é obrigatória");
        if (dto.getNomeFantasia() == null || dto.getNomeFantasia().isBlank()) throw new ValidationException("Nome fantasia é obrigatório");
        if (empresaRepository.existsByCnpj(dto.getCnpj())) throw new ConflictException("CNPJ já cadastrado no sistema");
        if (empresaRepository.existsByOrganizadorId(dto.getOrganizadorId())) throw new ConflictException("Este organizador já possui uma empresa vinculada");

        Empresa empresa = EmpresaMapper.toEntity(dto);
        empresa = empresaRepository.save(empresa);
        return EmpresaMapper.toDTO(empresa);
    }

    @Transactional(readOnly = true)
    public EmpresaResponseDTO buscarPorOrganizadorId(Long organizadorId) {
        if (organizadorId == null || organizadorId <= 0) throw new ValidationException("ID do organizador inválido");
        return empresaRepository.findByOrganizadorId(organizadorId)
                .map(EmpresaMapper::toDTO).orElse(null);
    }

    public boolean cnpjExiste(String cnpj) {
        return cnpj != null && !cnpj.isBlank() && empresaRepository.existsByCnpj(cnpj);
    }

    public boolean organizadorTemEmpresa(Long organizadorId) {
        return organizadorId != null && organizadorId > 0 && empresaRepository.existsByOrganizadorId(organizadorId);
    }

    @Transactional(readOnly = true)
    public Empresa buscarEntidadePorOrganizadorId(Long organizadorId) {
        return empresaRepository.findByOrganizadorId(organizadorId).orElse(null);
    }
}

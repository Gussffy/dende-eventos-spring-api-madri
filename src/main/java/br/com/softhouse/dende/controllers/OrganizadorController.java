package br.com.softhouse.dende.controllers;

import br.com.softhouse.dende.dto.request.OrganizadorRequestDTO;
import br.com.softhouse.dende.dto.request.StatusChangeRequestDTO;
import br.com.softhouse.dende.dto.response.ApiResponse;
import br.com.softhouse.dende.dto.response.OrganizadorResponseDTO;
import br.com.softhouse.dende.exceptions.ValidationException;
import br.com.softhouse.dende.services.OrganizadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organizadores")
@RequiredArgsConstructor
public class OrganizadorController {

    private final OrganizadorService organizadorService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizadorResponseDTO>> cadastrar(@RequestBody OrganizadorRequestDTO dto) {
        OrganizadorResponseDTO response = organizadorService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(response, "Organizador cadastrado com sucesso", 201));
    }

    @PutMapping("/{organizadorId}")
    public ResponseEntity<ApiResponse<OrganizadorResponseDTO>> alterar(
            @PathVariable Long organizadorId,
            @RequestBody OrganizadorRequestDTO dto) {
        OrganizadorResponseDTO response = organizadorService.atualizar(organizadorId, dto);
        return ResponseEntity.ok(new ApiResponse<>(response, "Organizador atualizado com sucesso", 200));
    }

    @GetMapping("/{organizadorId}")
    public ResponseEntity<ApiResponse<OrganizadorResponseDTO>> visualizar(@PathVariable Long organizadorId) {
        OrganizadorResponseDTO response = organizadorService.buscarPorId(organizadorId);
        return ResponseEntity.ok(new ApiResponse<>(response, "Organizador encontrado", 200));
    }

    @PatchMapping("/{organizadorId}/{status}")
    public ResponseEntity<ApiResponse<OrganizadorResponseDTO>> alterarStatus(
            @PathVariable Long organizadorId,
            @PathVariable boolean status,
            @RequestBody StatusChangeRequestDTO request) {
        if (request == null || request.getSenha() == null || request.getSenha().isBlank()) {
            throw new ValidationException("Senha é obrigatória");
        }

        OrganizadorResponseDTO response;
        String operacao;

        if (status) {
            response = organizadorService.ativarComSenha(organizadorId, request.getSenha());
            operacao = "ativado";
        } else {
            response = organizadorService.desativarComSenha(organizadorId, request.getSenha());
            operacao = "desativado";
        }

        return ResponseEntity.ok(new ApiResponse<>(response, "Organizador " + operacao + " com sucesso", 200));
    }
}

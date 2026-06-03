package br.com.softhouse.dende.controllers;

import br.com.softhouse.dende.dto.request.StatusChangeRequestDTO;
import br.com.softhouse.dende.dto.request.UsuarioRequestDTO;
import br.com.softhouse.dende.dto.response.ApiResponse;
import br.com.softhouse.dende.dto.response.UsuarioResponseDTO;
import br.com.softhouse.dende.exceptions.ValidationException;
import br.com.softhouse.dende.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> cadastrar(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(response, "Usuário cadastrado com sucesso", 201));
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> alterar(
            @PathVariable Long usuarioId,
            @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.atualizar(usuarioId, dto);
        return ResponseEntity.ok(new ApiResponse<>(response, "Usuário atualizado com sucesso", 200));
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> visualizar(@PathVariable Long usuarioId) {
        UsuarioResponseDTO response = usuarioService.buscarPorId(usuarioId);
        return ResponseEntity.ok(new ApiResponse<>(response, "Usuário encontrado", 200));
    }

    @PatchMapping("/{usuarioId}/{status}")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> alterarStatus(
            @PathVariable Long usuarioId,
            @PathVariable boolean status,
            @RequestBody StatusChangeRequestDTO request) {
        if (request == null || request.getSenha() == null || request.getSenha().isBlank()) {
            throw new ValidationException("Senha é obrigatória");
        }

        UsuarioResponseDTO response;
        String operacao;

        if (status) {
            response = usuarioService.ativarComSenha(usuarioId, request.getSenha());
            operacao = "ativado";
        } else {
            response = usuarioService.desativarComSenha(usuarioId, request.getSenha());
            operacao = "desativado";
        }

        return ResponseEntity.ok(new ApiResponse<>(response, "Usuário " + operacao + " com sucesso", 200));
    }
}

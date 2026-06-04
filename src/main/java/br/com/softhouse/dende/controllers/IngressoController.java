package br.com.softhouse.dende.controllers;

import br.com.softhouse.dende.dto.request.CompraRequestDTO;
import br.com.softhouse.dende.dto.response.ApiResponse;
import br.com.softhouse.dende.dto.response.CancelamentoResponseDTO;
import br.com.softhouse.dende.dto.response.CompraResponseDTO;
import br.com.softhouse.dende.dto.response.IngressoResponseDTO;
import br.com.softhouse.dende.exceptions.ValidationException;
import br.com.softhouse.dende.services.IngressoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IngressoController {

    private final IngressoService ingressoService;

    @PostMapping("/organizadores/{organizadorId}/eventos/{eventoId}/ingressos")
    public ResponseEntity<ApiResponse<CompraResponseDTO>> comprar(
            @PathVariable Long organizadorId,
            @PathVariable Long eventoId,
            @RequestBody CompraRequestDTO request) {
        if (request == null || request.getUsuarioEmail() == null || request.getUsuarioEmail().isBlank()) {
            throw new ValidationException("Email do usuário é obrigatório");
        }
        CompraResponseDTO response = ingressoService.comprar(organizadorId, eventoId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(response, "Compra processada com sucesso", 201));
    }

    @PostMapping("/usuarios/{usuarioId}/ingressos/{ingressoId}")
    public ResponseEntity<ApiResponse<CancelamentoResponseDTO>> cancelar(
            @PathVariable Long usuarioId,
            @PathVariable Long ingressoId) {
        CancelamentoResponseDTO response = ingressoService.cancelar(usuarioId, ingressoId);
        return ResponseEntity.ok(new ApiResponse<>(response, "Cancelamento realizado com sucesso", 200));
    }

    @GetMapping("/usuarios/{usuarioId}/ingressos")
    public ResponseEntity<ApiResponse<List<IngressoResponseDTO>>> listar(@PathVariable Long usuarioId) {
        List<IngressoResponseDTO> ingressos = ingressoService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(new ApiResponse<>(ingressos, "Ingressos listados com sucesso", 200));
    }
}

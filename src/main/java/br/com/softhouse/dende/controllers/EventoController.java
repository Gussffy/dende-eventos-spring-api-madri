package br.com.softhouse.dende.controllers;

import br.com.softhouse.dende.dto.request.EventoRequestDTO;
import br.com.softhouse.dende.dto.response.ApiResponse;
import br.com.softhouse.dende.dto.response.EventoResponseDTO;
import br.com.softhouse.dende.dto.response.EventoResumoDTO;
import br.com.softhouse.dende.services.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping("/organizadores/{organizadorId}/eventos")
    public ResponseEntity<ApiResponse<EventoResponseDTO>> cadastrar(
            @PathVariable Long organizadorId,
            @RequestBody EventoRequestDTO dto) {
        EventoResponseDTO response = eventoService.cadastrar(organizadorId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(response, "Evento cadastrado com sucesso", 201));
    }

    @PutMapping("/organizadores/{organizadorId}/eventos/{eventoId}")
    public ResponseEntity<ApiResponse<EventoResponseDTO>> alterar(
            @PathVariable Long organizadorId,
            @PathVariable Long eventoId,
            @RequestBody EventoRequestDTO dto) {
        EventoResponseDTO response = eventoService.atualizar(organizadorId, eventoId, dto);
        return ResponseEntity.ok(new ApiResponse<>(response, "Evento atualizado com sucesso", 200));
    }

    @PatchMapping("/organizadores/{organizadorId}/eventos/{eventoId}/{status}")
    public ResponseEntity<ApiResponse<EventoResponseDTO>> alterarStatus(
            @PathVariable Long organizadorId,
            @PathVariable Long eventoId,
            @PathVariable boolean status) {
        EventoResponseDTO response;
        String operacao;

        if (status) {
            response = eventoService.ativar(organizadorId, eventoId);
            operacao = "ativado";
        } else {
            response = eventoService.desativar(organizadorId, eventoId);
            operacao = "desativado";
        }

        return ResponseEntity.ok(new ApiResponse<>(response, "Evento " + operacao + " com sucesso", 200));
    }

    @GetMapping("/organizadores/{organizadorId}/eventos")
    public ResponseEntity<ApiResponse<List<EventoResumoDTO>>> listarDoOrganizador(
            @PathVariable Long organizadorId) {
        List<EventoResumoDTO> resumos = eventoService.listarPorOrganizador(organizadorId);
        return ResponseEntity.ok(new ApiResponse<>(resumos, "Eventos listados com sucesso", 200));
    }

    @GetMapping("/eventos")
    public ResponseEntity<ApiResponse<List<EventoResponseDTO>>> feed() {
        List<EventoResponseDTO> response = eventoService.feedAtivos();
        return ResponseEntity.ok(new ApiResponse<>(response, "Feed de eventos carregado", 200));
    }
}

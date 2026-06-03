package br.com.softhouse.dende.dto.response;

import br.com.softhouse.dende.model.enums.StatusIngresso;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngressoResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long eventoId;
    private String eventoNome;
    private LocalDateTime dataEvento;
    private String local;
    private String codigo;
    private LocalDateTime dataCompra;
    private String dataCompraFormatada;
    private Double valorPago;
    private StatusIngresso status;
    private Boolean ingressoPrincipal;
}

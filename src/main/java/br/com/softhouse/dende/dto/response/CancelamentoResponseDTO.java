package br.com.softhouse.dende.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelamentoResponseDTO {
    private String mensagem;
    private Double valorPago;
    private Double valorReembolsado;
    private String codigoIngresso;
}

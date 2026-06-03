package br.com.softhouse.dende.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequestDTO {
    private String usuarioEmail;
    private Double totalPago;
}

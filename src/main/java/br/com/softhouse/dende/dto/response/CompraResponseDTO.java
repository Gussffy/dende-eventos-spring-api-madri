package br.com.softhouse.dende.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDTO {
    private String mensagem;
    private List<String> codigosIngressos;
    private Double valorTotal;
    private String status;
}

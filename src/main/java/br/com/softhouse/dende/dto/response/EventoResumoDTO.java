package br.com.softhouse.dende.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoResumoDTO {
    private Long id;
    private String nome;
    private String periodo;
    private String local;
    private Double precoIngresso;
    private Integer ingressosVendidos;
    private Integer capacidadeMaxima;
    private Boolean ativo;
}

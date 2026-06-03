package br.com.softhouse.dende.dto.response;

import br.com.softhouse.dende.model.enums.ModalidadeEvento;
import br.com.softhouse.dende.model.enums.TipoEvento;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoResponseDTO {
    private Long id;
    private Long organizadorId;
    private String nome;
    private String pagina;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFinal;
    private String periodo;
    private TipoEvento tipoEvento;
    private Long eventoPrincipalId;
    private ModalidadeEvento modalidade;
    private Integer capacidadeMaxima;
    private String local;
    private Boolean ativo;
    private Double precoIngresso;
    private Boolean estornaCancelamento;
    private Double taxaEstorno;
    private Integer ingressosVendidos;
    private Integer ingressosDisponiveis;
}

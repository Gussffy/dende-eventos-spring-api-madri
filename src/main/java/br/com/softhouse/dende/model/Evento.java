package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.enums.ModalidadeEvento;
import br.com.softhouse.dende.model.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "evento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    @Column(name = "evento_principal_id")
    private Long eventoPrincipalId;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "pagina_web", length = 500)
    private String pagina;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 30)
    private TipoEvento tipoEvento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ModalidadeEvento modalidade;

    @Column(name = "local_evento", nullable = false, length = 500)
    private String local;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFinal;

    @Column(name = "capacidade_maxima", nullable = false)
    private Integer capacidadeMaxima;

    @Column(columnDefinition = "DECIMAL(10,2)", name = "preco_ingresso", nullable = false)
    private Double precoIngresso;

    @Column(name = "estorna_ingresso", nullable = false)
    private Boolean estornaCancelamento = true;

    @Column(columnDefinition = "DECIMAL(10,2)", name = "taxa_estorno", nullable = false)
    private Double taxaEstorno = 0.0;

    @Column(nullable = false)
    private Boolean ativo = false;

    @Transient
    private Integer ingressosVendidos = 0;

    public boolean validarDatas() {
        LocalDateTime agora = LocalDateTime.now();
        if (dataInicio.isBefore(agora)) return false;
        if (dataFinal.isBefore(dataInicio)) return false;
        long duracaoMinutos = Duration.between(dataInicio, dataFinal).toMinutes();
        return duracaoMinutos >= 30;
    }

    public boolean temIngressosDisponiveis() {
        return ingressosVendidos < capacidadeMaxima;
    }

    public int ingressosDisponiveis() {
        return capacidadeMaxima - ingressosVendidos;
    }

    public boolean eventoJaAconteceu() {
        return dataFinal.isBefore(LocalDateTime.now());
    }

    public boolean eventoEmAndamento() {
        LocalDateTime agora = LocalDateTime.now();
        return agora.isAfter(dataInicio) && agora.isBefore(dataFinal);
    }

    public boolean podeSerAtivado() {
        return validarDatas() && !ativo;
    }

    public Double calcularReembolso(Double valorPago) {
        if (!estornaCancelamento) return 0.0;
        return valorPago * (1 - taxaEstorno / 100);
    }

    public void venderIngresso() {
        if (temIngressosDisponiveis()) {
            this.ingressosVendidos++;
        }
    }

    public void cancelarIngresso() {
        if (this.ingressosVendidos > 0) {
            this.ingressosVendidos--;
        }
    }

    public String getPeriodoFormatado() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm ");
        return dataInicio.format(formatter) + " até " + dataFinal.format(formatter);
    }
}

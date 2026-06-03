package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.enums.StatusIngresso;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "ingresso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "evento_id", nullable = false)
    private Long eventoId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(name = "data_compra", nullable = false)
    private LocalDateTime dataCompra;

    @Column(name = "valor_pago", nullable = false)
    private Double valorPago;

    @Column(name = "valor_estornado", nullable = false)
    private Double valorEstornado = 0.0;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusIngresso status = StatusIngresso.PENDENTE;

    @Column(name = "ingresso_principal", nullable = false)
    private Boolean ingressoPrincipal = true;

    public Ingresso(Long usuarioId, Long eventoId, Double valorPago) {
        this.usuarioId = usuarioId;
        this.eventoId = eventoId;
        this.valorPago = valorPago;
        this.codigo = UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 12);
        this.dataCompra = LocalDateTime.now();
        this.status = StatusIngresso.PENDENTE;
        this.valorEstornado = 0.0;
        this.ingressoPrincipal = true;
    }

    public boolean podeSerCancelado() {
        return status.podeSerCancelado();
    }

    public void cancelar() {
        if (podeSerCancelado()) {
            this.status = StatusIngresso.CANCELADO;
        }
    }

    public void confirmarPagamento() {
        if (status == StatusIngresso.PENDENTE) {
            this.status = StatusIngresso.ATIVO;
        }
    }

    public void reembolsar() {
        if (status == StatusIngresso.ATIVO || status == StatusIngresso.CANCELADO) {
            this.status = StatusIngresso.REEMBOLSADO;
            this.valorEstornado = this.valorPago;
            this.dataCancelamento = LocalDateTime.now();
        }
    }

    public String getDataCompraFormatada() {
        if (dataCompra == null) return "";
        return dataCompra.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}

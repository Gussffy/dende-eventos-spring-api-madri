package br.com.softhouse.dende.model.builders;

import br.com.softhouse.dende.exceptions.ValidationException;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.enums.ModalidadeEvento;
import br.com.softhouse.dende.model.enums.TipoEvento;

import java.time.LocalDateTime;

public class EventoBuilder {

    private Long organizadorId;
    private String nome;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFinal;
    private TipoEvento tipoEvento;
    private ModalidadeEvento modalidade;
    private Integer capacidadeMaxima;
    private String local;
    private Double precoIngresso;

    private String pagina = "";
    private String descricao = "";
    private Long eventoPrincipalId = null;
    private Boolean estornaCancelamento = true;
    private Double taxaEstorno = 0.0;
    private Boolean ativo = false;
    private Integer ingressosVendidos = 0;

    private EventoBuilder() {}

    public static EventoBuilder builder() { return new EventoBuilder(); }

    public EventoBuilder organizadorId(Long v)          { this.organizadorId = v; return this; }
    public EventoBuilder nome(String v)                 { this.nome = v; return this; }
    public EventoBuilder pagina(String v)               { this.pagina = v; return this; }
    public EventoBuilder descricao(String v)            { this.descricao = v; return this; }
    public EventoBuilder dataInicio(LocalDateTime v)    { this.dataInicio = v; return this; }
    public EventoBuilder dataFinal(LocalDateTime v)     { this.dataFinal = v; return this; }
    public EventoBuilder tipoEvento(TipoEvento v)       { this.tipoEvento = v; return this; }
    public EventoBuilder eventoPrincipalId(Long v)      { this.eventoPrincipalId = v; return this; }
    public EventoBuilder modalidade(ModalidadeEvento v) { this.modalidade = v; return this; }
    public EventoBuilder capacidadeMaxima(Integer v)    { this.capacidadeMaxima = v; return this; }
    public EventoBuilder local(String v)                { this.local = v; return this; }
    public EventoBuilder ativo(Boolean v)               { this.ativo = v; return this; }
    public EventoBuilder precoIngresso(Double v)        { this.precoIngresso = v; return this; }
    public EventoBuilder estornaCancelamento(Boolean v) { this.estornaCancelamento = v; return this; }
    public EventoBuilder taxaEstorno(Double v)          { this.taxaEstorno = v; return this; }
    public EventoBuilder ingressosVendidos(Integer v)   { this.ingressosVendidos = v; return this; }

    public Evento build() {
        if (organizadorId == null) throw new ValidationException("organizadorId é obrigatório");
        if (nome == null || nome.isBlank()) throw new ValidationException("nome é obrigatório");
        if (dataInicio == null) throw new ValidationException("dataInicio é obrigatória");
        if (dataFinal == null) throw new ValidationException("dataFinal é obrigatória");
        if (tipoEvento == null) throw new ValidationException("tipoEvento é obrigatório");
        if (modalidade == null) throw new ValidationException("modalidade é obrigatória");
        if (capacidadeMaxima == null || capacidadeMaxima <= 0) throw new ValidationException("capacidadeMaxima deve ser maior que zero");
        if (local == null || local.isBlank()) throw new ValidationException("local é obrigatório");
        if (precoIngresso == null || precoIngresso < 0) throw new ValidationException("precoIngresso deve ser maior ou igual a zero");

        Evento evento = new Evento();
        evento.setOrganizadorId(organizadorId);
        evento.setNome(nome);
        evento.setPagina(pagina);
        evento.setDescricao(descricao);
        evento.setDataInicio(dataInicio);
        evento.setDataFinal(dataFinal);
        evento.setTipoEvento(tipoEvento);
        evento.setEventoPrincipalId(eventoPrincipalId);
        evento.setModalidade(modalidade);
        evento.setCapacidadeMaxima(capacidadeMaxima);
        evento.setLocal(local);
        evento.setAtivo(ativo);
        evento.setPrecoIngresso(precoIngresso);
        evento.setEstornaCancelamento(estornaCancelamento);
        evento.setTaxaEstorno(taxaEstorno);
        evento.setIngressosVendidos(ingressosVendidos);
        return evento;
    }
}

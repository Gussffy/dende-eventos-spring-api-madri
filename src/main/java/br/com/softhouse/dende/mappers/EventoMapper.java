package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.dto.request.EventoRequestDTO;
import br.com.softhouse.dende.dto.response.EventoResponseDTO;
import br.com.softhouse.dende.dto.response.EventoResumoDTO;
import br.com.softhouse.dende.model.Evento;

public class EventoMapper {

    private EventoMapper() {}

    public static EventoResponseDTO toDTO(Evento evento) {
        if (evento == null) return null;
        EventoResponseDTO dto = new EventoResponseDTO();
        dto.setId(evento.getId());
        dto.setOrganizadorId(evento.getOrganizadorId());
        dto.setNome(evento.getNome());
        dto.setPagina(evento.getPagina());
        dto.setDescricao(evento.getDescricao());
        dto.setDataInicio(evento.getDataInicio());
        dto.setDataFinal(evento.getDataFinal());
        dto.setPeriodo(evento.getPeriodoFormatado());
        dto.setTipoEvento(evento.getTipoEvento());
        dto.setEventoPrincipalId(evento.getEventoPrincipalId());
        dto.setModalidade(evento.getModalidade());
        dto.setCapacidadeMaxima(evento.getCapacidadeMaxima());
        dto.setLocal(evento.getLocal());
        dto.setAtivo(evento.getAtivo());
        dto.setPrecoIngresso(evento.getPrecoIngresso());
        dto.setEstornaCancelamento(evento.getEstornaCancelamento());
        dto.setTaxaEstorno(evento.getTaxaEstorno());
        dto.setIngressosVendidos(evento.getIngressosVendidos());
        dto.setIngressosDisponiveis(evento.ingressosDisponiveis());
        return dto;
    }

    public static EventoResumoDTO toResumoDTO(Evento evento) {
        if (evento == null) return null;
        EventoResumoDTO dto = new EventoResumoDTO();
        dto.setId(evento.getId());
        dto.setNome(evento.getNome());
        dto.setPeriodo(evento.getPeriodoFormatado());
        dto.setLocal(evento.getLocal());
        dto.setPrecoIngresso(evento.getPrecoIngresso());
        dto.setIngressosVendidos(evento.getIngressosVendidos());
        dto.setCapacidadeMaxima(evento.getCapacidadeMaxima());
        dto.setAtivo(evento.getAtivo());
        return dto;
    }

    public static Evento updateEntity(Evento evento, EventoRequestDTO dto) {
        if (dto == null) return evento;
        if (dto.getNome() != null) evento.setNome(dto.getNome());
        if (dto.getPagina() != null) evento.setPagina(dto.getPagina());
        if (dto.getDescricao() != null) evento.setDescricao(dto.getDescricao());
        if (dto.getDataInicio() != null) evento.setDataInicio(dto.getDataInicio());
        if (dto.getDataFinal() != null) evento.setDataFinal(dto.getDataFinal());
        if (dto.getTipoEvento() != null) evento.setTipoEvento(dto.getTipoEvento());
        if (dto.getEventoPrincipalId() != null) evento.setEventoPrincipalId(dto.getEventoPrincipalId());
        if (dto.getModalidade() != null) evento.setModalidade(dto.getModalidade());
        if (dto.getCapacidadeMaxima() != null) evento.setCapacidadeMaxima(dto.getCapacidadeMaxima());
        if (dto.getLocal() != null) evento.setLocal(dto.getLocal());
        if (dto.getPrecoIngresso() != null) evento.setPrecoIngresso(dto.getPrecoIngresso());
        if (dto.getEstornaCancelamento() != null) evento.setEstornaCancelamento(dto.getEstornaCancelamento());
        if (dto.getTaxaEstorno() != null) evento.setTaxaEstorno(dto.getTaxaEstorno());
        if (dto.getAtivo() != null) evento.setAtivo(dto.getAtivo());
        return evento;
    }
}

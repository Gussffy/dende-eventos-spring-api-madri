package br.com.softhouse.dende.model.enums;

public enum Sexo {

    M("Masculino"),
    F("Feminino"),
    O("Outro");

    private final String descricao;

    Sexo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
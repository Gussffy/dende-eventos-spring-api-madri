package br.com.softhouse.dende.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {

    private String mensagem;
    private int statusCode;
    private T dados;
    private String erro;
    private long timestamp;

    // Sucesso
    public ApiResponse(T dados, String mensagem, int statusCode) {
        this.dados = dados;
        this.mensagem = mensagem;
        this.statusCode = statusCode;
        this.erro = null;
        this.timestamp = System.currentTimeMillis();
    }

    // Erro
    public ApiResponse(String mensagem, int statusCode, String erro) {
        this.dados = null;
        this.mensagem = mensagem;
        this.statusCode = statusCode;
        this.erro = erro;
        this.timestamp = System.currentTimeMillis();
    }
}

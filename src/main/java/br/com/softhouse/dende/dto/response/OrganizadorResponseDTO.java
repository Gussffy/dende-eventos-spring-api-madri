package br.com.softhouse.dende.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizadorResponseDTO {
    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String idade;
    private String sexo; // agora retorna a descrição completa (e.g. "Masculino")
    private String email;
    private Boolean ativo;
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
}

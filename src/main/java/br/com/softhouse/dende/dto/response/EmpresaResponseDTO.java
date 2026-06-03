package br.com.softhouse.dende.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmpresaResponseDTO {
    private Long id;
    private Long organizadorId;
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
}

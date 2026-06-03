package br.com.softhouse.dende.dto.response;

import br.com.softhouse.dende.model.enums.Sexo;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String idade;
    private Sexo sexo;
    private String email;
    private Boolean ativo;
}

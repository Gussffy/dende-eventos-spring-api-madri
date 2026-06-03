package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.enums.Sexo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String nome;

    @Column (name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false, columnDefinition = "CHAR(1)")
    private Sexo sexo;

    @Column (nullable = false, unique = true)
    private String email;


    @Column(nullable = false)
    private String senha;

    @Column(name = "tipo_usuario", nullable = false)
    private String tipoUsuario = "COMUM";

    @Column(nullable = false)
    private Boolean ativo = true;

    @Transient
    public String getIdade() {
        if (dataNascimento == null) return "";
        Period periodo = Period.between(dataNascimento, LocalDate.now());
        return periodo.getYears() + " anos, " + periodo.getMonths() + " meses, " + periodo.getDays() + " dias";
    }
}

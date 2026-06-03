package br.com.softhouse.dende.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeRequestDTO {
    private String senha;
    private Long eventoId;
}

package com.ticketflow.catalogo.model.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisponibilidadeEventoDTO {
    private String id;
    private Integer assentosDisponiveis;
}

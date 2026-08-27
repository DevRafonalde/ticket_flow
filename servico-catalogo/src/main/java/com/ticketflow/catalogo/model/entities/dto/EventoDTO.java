package com.ticketflow.catalogo.model.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventoDTO {
    private String id;
    private String nome;
    private String local;
    private LocalDateTime dataEvento;
    private Integer totalAssentos;
    private Integer assentosDisponiveis;
}

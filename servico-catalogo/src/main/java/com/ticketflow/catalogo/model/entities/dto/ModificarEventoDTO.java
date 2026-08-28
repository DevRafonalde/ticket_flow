package com.ticketflow.catalogo.model.entities.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModificarEventoDTO {
    @NotBlank(message = "nome é obrigatório")
    private String nome;

    @NotBlank(message = "local é obrigatório")
    private String local;

    @NotNull(message = "dataEvento é obrigatória")
    @Future(message = "dataEvento não pode estar no passado")
    private LocalDateTime dataEvento;

    @NotNull(message = "totalAssentos é obrigatório")
    @Positive(message = "totalAssentos deve ser maior que zero")
    private Integer totalAssentos;
}
package com.ticketflow.catalogo.model.entities.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservarAssentosDTO {

    @NotNull(message = "quantidade é obrigatória")
    @Positive(message = "quantidade deve ser maior que zero")
    private Integer quantidade;
}

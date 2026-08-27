package com.ticketflow.catalogo.model.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginaEventos {
    private List<EventoDTO> conteudo;
    private int pagina;
    private int tamanho;
    private long totalElementos;
    private int totalPaginas;
}

package com.ticketflow.catalogo.controller;

import com.ticketflow.catalogo.model.entities.dto.PaginaEventos;
import com.ticketflow.catalogo.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/catalogo/eventos")
@RequiredArgsConstructor
public class EventoController {
    private final EventoService eventoService;

    @ApiResponse(responseCode = "500", description = "Erro no servidor", content = @Content)
    @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginaEventos.class)))
    @Operation(
            summary = "Listar eventos paginados com filtros",
            description = "Retorna eventos paginados. O parâmetro 'nome' realiza busca parcial pelo nome do evento, " +
                    "e 'de'/'ate' filtram pelo período da data do evento. Não exige autenticação."
    )
    @GetMapping
    public ResponseEntity<PaginaEventos> buscarTodos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) {
        PaginaEventos paginaEventos = eventoService.buscarEventos(pagina, tamanho, nome, de, ate);
        return ResponseEntity.ok(paginaEventos);
    }
}

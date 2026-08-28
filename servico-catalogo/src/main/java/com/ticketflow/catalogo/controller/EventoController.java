package com.ticketflow.catalogo.controller;

import com.ticketflow.catalogo.model.entities.dto.DisponibilidadeEventoDTO;
import com.ticketflow.catalogo.model.entities.dto.EventoDTO;
import com.ticketflow.catalogo.model.entities.dto.PaginaEventos;
import com.ticketflow.catalogo.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @ApiResponse(responseCode = "500", description = "Erro no servidor", content = @Content)
    @ApiResponse(responseCode = "404", description = "Evento não encontrado (EVENTO_NAO_ENCONTRADO)", content = @Content)
    @ApiResponse(responseCode = "200", description = "Evento retornado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventoDTO.class)))
    @Operation(
            summary = "Buscar evento por id",
            description = "Retorna o detalhe de um evento pelo seu id. Não exige autenticação."
    )
    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> buscarDetalhado(@PathVariable String id) {
        EventoDTO evento = eventoService.buscarPorId(id);
        return ResponseEntity.ok(evento);
    }

    @ApiResponse(responseCode = "500", description = "Erro no servidor", content = @Content)
    @ApiResponse(responseCode = "404", description = "Evento não encontrado (EVENTO_NAO_ENCONTRADO)", content = @Content)
    @ApiResponse(responseCode = "200", description = "Disponibilidade retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisponibilidadeEventoDTO.class)))
    @Operation(
            summary = "Buscar disponibilidade de um evento por id",
            description = "Retorna a quantidade de assentos disponíveis em um evento pelo seu id. Não exige autenticação." +
                    "Endpoint cacheado."
    )
    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<DisponibilidadeEventoDTO> buscarDisponibilidadeEvento(@PathVariable String id) {
        DisponibilidadeEventoDTO evento = eventoService.buscarDisponibilidadeEvento(id);
        return ResponseEntity.ok(evento);
    }
}

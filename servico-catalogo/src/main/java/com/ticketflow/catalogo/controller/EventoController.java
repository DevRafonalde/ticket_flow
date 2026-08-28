package com.ticketflow.catalogo.controller;

import com.ticketflow.catalogo.model.entities.dto.CriarEventoDTO;
import com.ticketflow.catalogo.model.entities.dto.DisponibilidadeEventoDTO;
import com.ticketflow.catalogo.model.entities.dto.EventoDTO;
import com.ticketflow.catalogo.model.entities.dto.PaginaEventos;
import com.ticketflow.catalogo.security.JwtService;
import com.ticketflow.catalogo.security.UsuarioAutenticado;
import com.ticketflow.catalogo.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/catalogo/eventos")
@RequiredArgsConstructor
public class EventoController {
    private final EventoService eventoService;
    private final JwtService jwtService;

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

    @ApiResponse(responseCode = "500", description = "Erro no servidor", content = @Content)
    @ApiResponse(responseCode = "400", description = "Dados inválidos, ex: data no passado (VALIDACAO_ERRO)", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente, mal formatado ou expirado (AUTENTICACAO_TOKEN_EXPIRADO)", content = @Content)
    @ApiResponse(responseCode = "403", description = "Usuário autenticado sem papel ORGANIZADOR/ADMIN (AUTENTICACAO_ACESSO_NEGADO)", content = @Content)
    @ApiResponse(responseCode = "201", description = "Evento criado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventoDTO.class)))
    @Operation(
            summary = "Criar evento",
            description = "Cria um novo evento com todos os assentos disponíveis. Requer token JWT (emitido pelo "
                    + "servico-autenticacao) de um usuário com papel ORGANIZADOR ou ADMIN."
    )
    @PostMapping
    public ResponseEntity<EventoDTO> criar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String autorizacao,
            @Valid @RequestBody CriarEventoDTO dto) {
        // 1) Quem está chamando? O JwtService confere localmente (sem chamar o servico-autenticacao)
        //    que o JWT foi assinado com o segredo compartilhado e ainda não expirou, e devolve o id
        //    e o papel do usuário embutidos no token (claims "sub" e "papel").
        UsuarioAutenticado usuario = jwtService.autenticar(autorizacao);

        // 2) Autorização (papel) + regras de negócio da criação ficam no service - ver EventoService.
        EventoDTO criado = eventoService.criarEvento(usuario, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }
}

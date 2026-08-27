package com.ticketflow.catalogo.controller;

import com.ticketflow.catalogo.model.entities.dto.EventoDTO;
import com.ticketflow.catalogo.model.entities.dto.PaginaEventos;
import com.ticketflow.catalogo.service.EventoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventoController.class)
class EventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventoService eventoService;

    private PaginaEventos paginaExemplo() {
        EventoDTO evento = EventoDTO.builder()
                .id("a1b2c3d4-0000-0000-0000-000000000000")
                .nome("Show da Banda X")
                .local("Arena Y")
                .dataEvento(LocalDateTime.of(2026, 11, 20, 22, 0))
                .totalAssentos(5000)
                .assentosDisponiveis(1200)
                .build();

        return PaginaEventos.builder()
                .conteudo(List.of(evento))
                .pagina(0)
                .tamanho(20)
                .totalElementos(134)
                .totalPaginas(7)
                .build();
    }

    @Test
    void buscarTodos_deveRetornar200ComValoresPadraoQuandoSemParametros() throws Exception {
        when(eventoService.buscarEventos(0, 20, null, null, null)).thenReturn(paginaExemplo());

        mockMvc.perform(get("/api/catalogo/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagina").value(0))
                .andExpect(jsonPath("$.tamanho").value(20))
                .andExpect(jsonPath("$.totalElementos").value(134))
                .andExpect(jsonPath("$.totalPaginas").value(7))
                .andExpect(jsonPath("$.conteudo[0].id").value("a1b2c3d4-0000-0000-0000-000000000000"))
                .andExpect(jsonPath("$.conteudo[0].nome").value("Show da Banda X"))
                .andExpect(jsonPath("$.conteudo[0].local").value("Arena Y"))
                .andExpect(jsonPath("$.conteudo[0].totalAssentos").value(5000))
                .andExpect(jsonPath("$.conteudo[0].assentosDisponiveis").value(1200));

        verify(eventoService).buscarEventos(0, 20, null, null, null);
    }

    @Test
    void buscarTodos_deveRepassarParametrosDePaginacaoEFiltrosParaOService() throws Exception {
        when(eventoService.buscarEventos(eq(2), eq(5), eq("Banda"), eq(LocalDate.of(2026, 1, 1)), eq(LocalDate.of(2026, 12, 31))))
                .thenReturn(paginaExemplo());

        mockMvc.perform(get("/api/catalogo/eventos")
                        .param("pagina", "2")
                        .param("tamanho", "5")
                        .param("nome", "Banda")
                        .param("de", "2026-01-01")
                        .param("ate", "2026-12-31"))
                .andExpect(status().isOk());

        verify(eventoService).buscarEventos(2, 5, "Banda", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
    }

    @Test
    void buscarTodos_deveAceitarApenasOFiltroDeNome() throws Exception {
        when(eventoService.buscarEventos(eq(0), eq(20), eq("Banda"), isNull(), isNull()))
                .thenReturn(paginaExemplo());

        mockMvc.perform(get("/api/catalogo/eventos").param("nome", "Banda"))
                .andExpect(status().isOk());

        verify(eventoService).buscarEventos(0, 20, "Banda", null, null);
    }

    @Test
    void buscarTodos_deveRetornar200ComListaVaziaQuandoNaoHaEventos() throws Exception {
        PaginaEventos paginaVazia = PaginaEventos.builder()
                .conteudo(List.of())
                .pagina(0)
                .tamanho(20)
                .totalElementos(0)
                .totalPaginas(0)
                .build();
        when(eventoService.buscarEventos(0, 20, null, null, null)).thenReturn(paginaVazia);

        mockMvc.perform(get("/api/catalogo/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo").isEmpty())
                .andExpect(jsonPath("$.totalElementos").value(0));
    }

    @Test
    void buscarTodos_deveRetornar400QuandoDataForInvalida() throws Exception {
        mockMvc.perform(get("/api/catalogo/eventos").param("de", "não-e-uma-data"))
                .andExpect(status().isBadRequest());
    }
}

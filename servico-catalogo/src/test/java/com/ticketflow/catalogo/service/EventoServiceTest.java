package com.ticketflow.catalogo.service;

import com.ticketflow.catalogo.model.entities.dto.EventoDTO;
import com.ticketflow.catalogo.model.entities.dto.PaginaEventos;
import com.ticketflow.catalogo.model.entities.orm.EventoORM;
import com.ticketflow.catalogo.model.repositories.EventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventoServiceTest {

    private final EventoRepository eventoRepository = mock(EventoRepository.class);
    private final ModelMapper modelMapper = new ModelMapper();
    private final EventoService eventoService = new EventoService(modelMapper, eventoRepository);

    private EventoORM eventoTeste;

    @BeforeEach
    void setUp() {
        eventoTeste = EventoORM.builder()
                .id("a1b2c3d4-0000-0000-0000-000000000000")
                .nome("Show da Banda X")
                .local("Arena Y")
                .dataEvento(LocalDateTime.of(2026, 11, 20, 22, 0))
                .totalAssentos(5000)
                .assentosDisponiveis(1200)
                .build();
    }

    @SuppressWarnings("unchecked")
    private void mockRepositoryReturn(Page<EventoORM> pagina) {
        when(eventoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pagina);
    }

    @Test
    void buscarEventos_deveMapearConteudoDaPaginaCorretamente() {
        Page<EventoORM> pageRetornada = new PageImpl<>(List.of(eventoTeste), PageRequest.of(0, 20), 1);
        mockRepositoryReturn(pageRetornada);

        PaginaEventos resultado = eventoService.buscarEventos(0, 20, null, null, null);

        assertThat(resultado.getConteudo()).hasSize(1);
        EventoDTO dto = resultado.getConteudo().get(0);
        assertThat(dto.getId()).isEqualTo(eventoTeste.getId());
        assertThat(dto.getNome()).isEqualTo(eventoTeste.getNome());
        assertThat(dto.getLocal()).isEqualTo(eventoTeste.getLocal());
        assertThat(dto.getDataEvento()).isEqualTo(eventoTeste.getDataEvento());
        assertThat(dto.getTotalAssentos()).isEqualTo(eventoTeste.getTotalAssentos());
        assertThat(dto.getAssentosDisponiveis()).isEqualTo(eventoTeste.getAssentosDisponiveis());
    }

    @Test
    void buscarEventos_devePreencherMetadadosDePaginacao() {
        Page<EventoORM> pageRetornada = new PageImpl<>(List.of(eventoTeste), PageRequest.of(1, 20), 134);
        mockRepositoryReturn(pageRetornada);

        PaginaEventos resultado = eventoService.buscarEventos(1, 20, null, null, null);

        assertThat(resultado.getPagina()).isEqualTo(1);
        assertThat(resultado.getTamanho()).isEqualTo(20);
        assertThat(resultado.getTotalElementos()).isEqualTo(134);
        assertThat(resultado.getTotalPaginas()).isEqualTo(7);
    }

    @Test
    void buscarEventos_deveRetornarConteudoVazioQuandoNaoHaResultados() {
        Page<EventoORM> pageVazia = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        mockRepositoryReturn(pageVazia);

        PaginaEventos resultado = eventoService.buscarEventos(0, 20, "inexistente", null, null);

        assertThat(resultado.getConteudo()).isEmpty();
        assertThat(resultado.getTotalElementos()).isZero();
        assertThat(resultado.getTotalPaginas()).isZero();
    }

    @Test
    @SuppressWarnings("unchecked")
    void buscarEventos_devePassarPaginaETamanhoComoPageRequestParaORepositorio() {
        Page<EventoORM> pageRetornada = new PageImpl<>(List.of(eventoTeste), PageRequest.of(2, 5), 11);
        mockRepositoryReturn(pageRetornada);

        eventoService.buscarEventos(2, 5, null, null, null);

        verify(eventoRepository).findAll(any(Specification.class), org.mockito.ArgumentMatchers.eq(PageRequest.of(2, 5)));
    }

    @Test
    void buscarEventos_devePropagarFiltrosParaSpecificationSemAlterarComportamentoDoRepositorio() {
        Page<EventoORM> pageRetornada = new PageImpl<>(List.of(eventoTeste), PageRequest.of(0, 20), 1);
        mockRepositoryReturn(pageRetornada);

        PaginaEventos resultado = eventoService.buscarEventos(
                0, 20, "Banda", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        assertThat(resultado.getConteudo()).hasSize(1);
        verify(eventoRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}

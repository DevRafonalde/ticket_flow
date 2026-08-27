package com.ticketflow.catalogo.service;

import com.ticketflow.catalogo.model.entities.dto.EventoDTO;
import com.ticketflow.catalogo.model.entities.dto.PaginaEventos;
import com.ticketflow.catalogo.model.entities.orm.EventoORM;
import com.ticketflow.catalogo.model.repositories.EventoRepository;
import com.ticketflow.catalogo.model.repositories.EventoSpecification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EventoService {
    private final ModelMapper modelMapper;
    private final EventoRepository eventoRepository;

    public PaginaEventos buscarEventos(int pagina, int tamanho, String nome, LocalDate de, LocalDate ate) {
        Page<EventoORM> paginaEncontrada = eventoRepository.findAll(
                EventoSpecification.comFiltros(nome, de, ate),
                PageRequest.of(pagina, tamanho)
        );

        return PaginaEventos.builder()
                .conteudo(paginaEncontrada.getContent().stream().map(eventoORM -> modelMapper.map(eventoORM, EventoDTO.class)).toList())
                .pagina(paginaEncontrada.getNumber())
                .tamanho(paginaEncontrada.getSize())
                .totalElementos(paginaEncontrada.getTotalElements())
                .totalPaginas(paginaEncontrada.getTotalPages())
                .build();
    }
}

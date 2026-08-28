package com.ticketflow.catalogo.service;

import com.ticketflow.catalogo.config.cache.CacheNames;
import com.ticketflow.catalogo.model.entities.dto.DisponibilidadeEventoDTO;
import com.ticketflow.catalogo.model.entities.dto.EventoDTO;
import com.ticketflow.catalogo.model.entities.dto.PaginaEventos;
import com.ticketflow.catalogo.model.entities.orm.EventoORM;
import com.ticketflow.catalogo.model.repositories.EventoRepository;
import com.ticketflow.catalogo.model.repositories.EventoSpecification;
import com.ticketflow.catalogo.exception.ElementoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EventoService {
    private final ModelMapper modelMapper;
    private final EventoRepository eventoRepository;

    @Cacheable(cacheNames = CacheNames.TODOS_EVENTOS, key = "#pagina + '-' + #tamanho + '-' + #nome + '-' + #de + '-' + #ate")
    public PaginaEventos buscarEventos(int pagina, int tamanho, String nome, LocalDate de, LocalDate ate) {
        Page<EventoORM> paginaEncontrada = eventoRepository.findAll(EventoSpecification.comFiltros(nome, de, ate), PageRequest.of(pagina, tamanho));

        return PaginaEventos.builder().conteudo(paginaEncontrada.getContent().stream()
                        .map(eventoORM -> modelMapper.map(eventoORM, EventoDTO.class))
                        .toList()
                )
                .pagina(paginaEncontrada.getNumber())
                .tamanho(paginaEncontrada.getSize())
                .totalElementos(paginaEncontrada.getTotalElements())
                .totalPaginas(paginaEncontrada.getTotalPages())
                .build();
    }

    @Cacheable(cacheNames = CacheNames.EVENTO_POR_ID, key = "#id")
    public EventoDTO buscarPorId(String id) {
        EventoORM evento = eventoRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Evento não encontrado para o id: " + id));
        return modelMapper.map(evento, EventoDTO.class);
    }

    @Cacheable(cacheNames = CacheNames.DISPONIBILIDADE_EVENTO, key = "#id")
    public DisponibilidadeEventoDTO buscarDisponibilidadeEvento(String id) {
        return eventoRepository.findDisponibilidadeById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Evento não encontrado para o id: " + id));
    }
}

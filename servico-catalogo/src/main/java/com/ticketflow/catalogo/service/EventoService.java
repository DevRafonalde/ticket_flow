package com.ticketflow.catalogo.service;

import com.ticketflow.catalogo.config.cache.CacheNames;
import com.ticketflow.catalogo.model.entities.dto.CriarEventoDTO;
import com.ticketflow.catalogo.model.entities.dto.DisponibilidadeEventoDTO;
import com.ticketflow.catalogo.model.entities.dto.EventoDTO;
import com.ticketflow.catalogo.model.entities.dto.PaginaEventos;
import com.ticketflow.catalogo.model.entities.orm.EventoORM;
import com.ticketflow.catalogo.model.repositories.EventoRepository;
import com.ticketflow.catalogo.model.repositories.EventoSpecification;
import com.ticketflow.catalogo.exception.AcessoNegadoException;
import com.ticketflow.catalogo.exception.ElementoNaoEncontradoException;
import com.ticketflow.catalogo.security.Papel;
import com.ticketflow.catalogo.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
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

    /**
     * Cria um evento novo. Quem chama ({@link com.ticketflow.catalogo.controller.EventoController})
     * já validou o JWT e resolveu {@code usuario} - aqui só resta checar se o papel autoriza a ação
     * (regra transversal: só ORGANIZADOR ou ADMIN criam evento, ver regras-de-negocio.md 1.4 do
     * servico-autenticacao). Diferente da disponibilidade (que muda devido a reservas feitas
     * no servico-reserva, um serviço externo, e por isso depende do listener de Pub/Sub em
     * {@link com.ticketflow.catalogo.config.cache.CacheEvictionListenerConfig}), a criação de
     * evento é uma escrita local a este serviço: a invalidação do cache de listagem
     * ({@code @CacheEvict}) acontece na mesma transação, sem precisar de nenhuma mensageria.
     */
    @CacheEvict(cacheNames = CacheNames.TODOS_EVENTOS, allEntries = true)
    public EventoDTO criarEvento(UsuarioAutenticado usuario, CriarEventoDTO dto) {
        if (usuario.papel() != Papel.ORGANIZADOR && usuario.papel() != Papel.ADMIN) {
            throw new AcessoNegadoException("Apenas organizadores ou administradores podem criar eventos.");
        }

        EventoORM evento = EventoORM.builder()
                .nome(dto.getNome())
                .local(dto.getLocal())
                .dataEvento(dto.getDataEvento())
                .totalAssentos(dto.getTotalAssentos())
                // No momento da criação ainda não há nenhuma reserva - todos os assentos
                // começam disponíveis. É o servico-reserva, mais tarde, quem vai reduzir
                // esse valor a cada reserva confirmada.
                .assentosDisponiveis(dto.getTotalAssentos())
                .organizadorId(usuario.id())
                .build();

        EventoORM salvo = eventoRepository.save(evento);
        return modelMapper.map(salvo, EventoDTO.class);
    }
}

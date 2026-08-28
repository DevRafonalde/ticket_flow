package com.ticketflow.catalogo.model.repositories;

import com.ticketflow.catalogo.model.entities.dto.DisponibilidadeEventoDTO;
import com.ticketflow.catalogo.model.entities.orm.EventoORM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EventoRepository extends JpaRepository<EventoORM, String>, JpaSpecificationExecutor<EventoORM> {
    /**
     * Projeção via query derivada do nome do método - o Spring Data gera a query com
     * base no construtor de {@link DisponibilidadeEventoDTO} (id, assentosDisponiveis),
     * casando por nome com as propriedades de {@link EventoORM}.
     */
    Optional<DisponibilidadeEventoDTO> findDisponibilidadeById(String id);
}

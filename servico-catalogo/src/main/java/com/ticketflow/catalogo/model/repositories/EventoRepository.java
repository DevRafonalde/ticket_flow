package com.ticketflow.catalogo.model.repositories;

import com.ticketflow.catalogo.model.entities.orm.EventoORM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventoRepository extends JpaRepository<EventoORM, String>, JpaSpecificationExecutor<EventoORM> {
}

package com.ticketflow.catalogo.model.repositories;

import com.ticketflow.catalogo.model.entities.orm.EventoORM;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
public class EventoSpecification {
    public static Specification<EventoORM> comFiltros(String nome, LocalDate de, LocalDate ate) {

        /*
        Caso chame
        EventoSpecification.comFiltros("Java", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31))

        É equivalente a um filtro
        WHERE LOWER(nome) LIKE '%java%' AND dataEvento >= '2026-08-01 00:00:00' AND dataEvento <= '2026-08-31 23:59:59.999999999'
        */

        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();

            if (nome != null && !nome.isBlank()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
            }

            if (de != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("dataEvento"), de.atStartOfDay()));
            }

            if (ate != null) {
                LocalDateTime fimDoDia = LocalDateTime.of(ate, LocalTime.MAX);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("dataEvento"), fimDoDia));
            }

            return predicate;
        };
    }
}

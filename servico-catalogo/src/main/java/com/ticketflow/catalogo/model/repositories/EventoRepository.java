package com.ticketflow.catalogo.model.repositories;

import com.ticketflow.catalogo.model.entities.dto.DisponibilidadeEventoDTO;
import com.ticketflow.catalogo.model.entities.orm.EventoORM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventoRepository extends JpaRepository<EventoORM, String>, JpaSpecificationExecutor<EventoORM> {
    /**
     * Projeção via query derivada do nome do método - o Spring Data gera a query com
     * base no construtor de {@link DisponibilidadeEventoDTO} (id, assentosDisponiveis),
     * casando por nome com as propriedades de {@link EventoORM}.
     */
    Optional<DisponibilidadeEventoDTO> findDisponibilidadeById(String id);

    /**
     * Decremento atômico e condicional de {@code assentosDisponiveis} - a checagem "tem assento
     * o suficiente?" e o desconto acontecem no mesmo UPDATE, então duas reservas concorrentes para
     * o último assento não conseguem as duas "ler 1 disponível, subtrair, salvar" e vender o mesmo
     * assento duas vezes (a segunda simplesmente não casa mais o {@code WHERE} depois da primeira
     * commitar). É por isso que isto é uma bulk update JPQL e não um find-then-save comum.
     * <p>
     * {@code ativo = true} é explícito porque {@code @SQLRestriction} da entidade não é aplicado
     * automaticamente a bulk updates/deletes JPQL (só a SELECTs via ORM normal).
     *
     * @return quantas linhas foram afetadas: 1 se descontou, 0 se o evento não existe/está
     * inativo, ou se não havia assentos suficientes - quem chama decide qual dos dois foi.
     */
    @Modifying
    @Query("UPDATE EventoORM e SET e.assentosDisponiveis = e.assentosDisponiveis - :quantidade "
            + "WHERE e.id = :id AND e.ativo = true AND e.assentosDisponiveis >= :quantidade")
    int reservarAssentos(@Param("id") String id, @Param("quantidade") int quantidade);
}

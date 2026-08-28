package com.ticketflow.catalogo.model.entities.orm;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
public class EventoORM {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "local")
    private String local;

    @Column(name = "data_evento")
    private LocalDateTime dataEvento;

    @Column(name = "total_assentos")
    private Integer totalAssentos;

    @Column(name = "assentos_disponiveis")
    private Integer assentosDisponiveis;

    /**
     * Id (sub do JWT) do usuário ORGANIZADOR que criou o evento. Não é exposto na API pública
     * (não faz parte do {@code EventoDTO}) - usado apenas internamente para a checagem de posse
     * em edição/exclusão (um ORGANIZADOR só mexe nos próprios eventos; ADMIN mexe em qualquer um).
     */
    @Column(name = "organizador_id")
    private String organizadorId;

    @CreatedDate
    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}

package com.ticketflow.catalogo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exclusão de evento com reservas em situação PENDENTE ou CONFIRMADA - ver regras-de-negocio.md 2.2.
 */
public class EventoPossuiReservasAtivasException extends NegocioException {

    public EventoPossuiReservasAtivasException(String mensagem) {
        super(HttpStatus.CONFLICT, "EVENTO_POSSUI_RESERVAS_ATIVAS", mensagem);
    }
}

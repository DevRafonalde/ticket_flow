package com.ticketflow.catalogo.exception;

import org.springframework.http.HttpStatus;

/**
 * Não há assentos suficientes disponíveis para a quantidade pedida - ver docs/api.md
 * (código EVENTO_ESGOTADO, ocorre na criação de reserva pelo servico-reserva).
 */
public class EventoEsgotadoException extends NegocioException {

    public EventoEsgotadoException(String mensagem) {
        super(HttpStatus.CONFLICT, "EVENTO_ESGOTADO", mensagem);
    }
}

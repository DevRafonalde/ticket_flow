package com.ticketflow.notificacao.exception;

import org.springframework.http.HttpStatus;

/**
 * Superclasse para exceções de negócio deste serviço.
 * Cada subclasse define o {@link HttpStatus} adequado à situação que representa.
 */
public abstract class NegocioException extends RuntimeException {

    private final HttpStatus status;

    protected NegocioException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    protected NegocioException(HttpStatus status, String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

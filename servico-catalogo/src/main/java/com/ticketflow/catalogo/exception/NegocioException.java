package com.ticketflow.catalogo.exception;

import org.springframework.http.HttpStatus;

/**
 * Superclasse para exceções de negócio deste serviço.
 * Cada subclasse define o {@link HttpStatus} e o {@code codigo} (valor do campo {@code erro}
 * na resposta padronizada - ver tabela de códigos em docs/api.md) adequados à situação que representa.
 */
public abstract class NegocioException extends RuntimeException {
    private final HttpStatus status;
    private final String codigo;

    protected NegocioException(HttpStatus status, String codigo, String mensagem) {
        super(mensagem);
        this.status = status;
        this.codigo = codigo;
    }

    protected NegocioException(HttpStatus status, String codigo, String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.status = status;
        this.codigo = codigo;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCodigo() {
        return codigo;
    }
}

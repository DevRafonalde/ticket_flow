package com.ticketflow.gateway.exception;

import org.springframework.http.HttpStatus;

public class ElementoNaoEncontradoException extends NegocioException {

    public ElementoNaoEncontradoException(String mensagem) {
        super(HttpStatus.NOT_FOUND, mensagem);
    }
}

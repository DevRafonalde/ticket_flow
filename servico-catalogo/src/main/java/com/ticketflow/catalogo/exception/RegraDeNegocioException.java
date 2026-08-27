package com.ticketflow.catalogo.exception;

import org.springframework.http.HttpStatus;

public class RegraDeNegocioException extends NegocioException {

    public RegraDeNegocioException(String mensagem) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, mensagem);
    }
}

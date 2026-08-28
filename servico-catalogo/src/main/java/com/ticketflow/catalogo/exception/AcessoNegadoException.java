package com.ticketflow.catalogo.exception;

import org.springframework.http.HttpStatus;

/**
 * Token válido, mas o papel do usuário (ou a posse do recurso) não autoriza a ação.
 */
public class AcessoNegadoException extends NegocioException {
    public AcessoNegadoException(String mensagem) {
        super(HttpStatus.FORBIDDEN, "AUTENTICACAO_ACESSO_NEGADO", mensagem);
    }
}
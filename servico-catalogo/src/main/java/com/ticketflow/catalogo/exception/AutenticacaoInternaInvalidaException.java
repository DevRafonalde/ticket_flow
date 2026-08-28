package com.ticketflow.catalogo.exception;

import org.springframework.http.HttpStatus;

/**
 * Chave de API interna (servico-a-servico) ausente ou incorreta - ver
 * {@link com.ticketflow.catalogo.security.InternalApiKeyService}. Não é sobre JWT de usuário.
 */
public class AutenticacaoInternaInvalidaException extends NegocioException {

    public AutenticacaoInternaInvalidaException(String mensagem) {
        super(HttpStatus.UNAUTHORIZED, "AUTENTICACAO_INTERNA_INVALIDA", mensagem);
    }
}

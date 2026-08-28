package com.ticketflow.catalogo.exception;

import org.springframework.http.HttpStatus;

/**
 * Header {@code Authorization} ausente/mal formatado, ou JWT com assinatura inválida ou expirado.
 * Ver {@link com.ticketflow.catalogo.security.JwtService}.
 */
public class TokenInvalidoException extends NegocioException {
    public TokenInvalidoException(String mensagem) {
        super(HttpStatus.UNAUTHORIZED, "AUTENTICACAO_TOKEN_EXPIRADO", mensagem);
    }
}
package com.ticketflow.catalogo.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Representa falhas de validação de entrada identificadas manualmente
 * (fora do fluxo padrão de Bean Validation), podendo carregar múltiplos detalhes.
 */
public class ValidacaoException extends NegocioException {

    private final List<String> detalhes;

    public ValidacaoException(String mensagem) {
        this(mensagem, List.of());
    }

    public ValidacaoException(String mensagem, List<String> detalhes) {
        super(HttpStatus.BAD_REQUEST, mensagem);
        this.detalhes = detalhes;
    }

    public List<String> getDetalhes() {
        return detalhes;
    }
}

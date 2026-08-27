package com.ticketflow.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Trata exceções lançadas por eventuais controllers/filters reativos deste serviço.
 * As rotas configuradas no Spring Cloud Gateway (proxy) não passam por aqui.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErroResposta> tratarValidacaoException(ValidacaoException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(ex.getStatus())
                .body(construirErro(ex.getStatus(), ex.getMessage(), exchange, ex.getDetalhes()));
    }

    @ExceptionHandler(ElementoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarElementoNaoEncontrado(ElementoNaoEncontradoException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(ex.getStatus())
                .body(construirErro(ex.getStatus(), ex.getMessage(), exchange, null));
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ErroResposta> tratarNegocioException(NegocioException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(ex.getStatus())
                .body(construirErro(ex.getStatus(), ex.getMessage(), exchange, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> tratarErroGenerico(Exception ex, ServerWebExchange exchange) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(construirErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor", exchange, null));
    }

    private ErroResposta construirErro(HttpStatus status, String mensagem, ServerWebExchange exchange, List<String> detalhes) {
        return new ErroResposta(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                exchange.getRequest().getPath().value(),
                detalhes
        );
    }
}

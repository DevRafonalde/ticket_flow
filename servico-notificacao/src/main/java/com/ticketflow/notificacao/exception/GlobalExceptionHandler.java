package com.ticketflow.notificacao.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErroResposta> tratarValidacaoException(ValidacaoException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getStatus())
                .body(construirErro(ex.getStatus(), ex.getMessage(), request, ex.getDetalhes()));
    }

    @ExceptionHandler(ElementoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarElementoNaoEncontrado(ElementoNaoEncontradoException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getStatus())
                .body(construirErro(ex.getStatus(), ex.getMessage(), request, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacaoBean(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        return tratarValidacaoException(new ValidacaoException("Dados inválidos na requisição", detalhes), request);
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ErroResposta> tratarNegocioException(NegocioException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getStatus())
                .body(construirErro(ex.getStatus(), ex.getMessage(), request, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> tratarErroGenerico(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(construirErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor", request, null));
    }

    private ErroResposta construirErro(HttpStatus status, String mensagem, HttpServletRequest request, List<String> detalhes) {
        return new ErroResposta(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI(),
                detalhes
        );
    }
}

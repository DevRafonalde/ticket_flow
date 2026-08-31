package com.ticketflow.catalogo.exception;

import org.springframework.http.HttpStatus;

/**
 * Liberação de assentos (POST /reservar inverso) tentaria deixar assentosDisponiveis maior que
 * totalAssentos - sinal de reserva liberada em duplicidade (ex: retry de rede do relay do outbox
 * do servico-reserva) ou de dessincronia entre os dois serviços. Mesma classe de problema que
 * {@link EventoEsgotadoException} (conflito com o estado atual do recurso), só que no sentido oposto.
 */
public class LimiteAssentosExcedidoException extends NegocioException {

    public LimiteAssentosExcedidoException(String mensagem) {
        super(HttpStatus.CONFLICT, "LIMITE_ASSENTOS_EXCEDIDO", mensagem);
    }
}

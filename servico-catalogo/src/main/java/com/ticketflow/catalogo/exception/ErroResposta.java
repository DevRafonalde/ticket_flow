package com.ticketflow.catalogo.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResposta(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes
) {
}

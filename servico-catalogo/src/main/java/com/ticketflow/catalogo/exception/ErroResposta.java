package com.ticketflow.catalogo.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResposta(
        LocalDateTime dataHora,
        int codigoStatus,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes
) {
}

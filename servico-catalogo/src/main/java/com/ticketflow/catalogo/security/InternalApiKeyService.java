package com.ticketflow.catalogo.security;

import com.ticketflow.catalogo.exception.AutenticacaoInternaInvalidaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Valida chamadas servico-a-servico (ex: servico-reserva reservando assentos aqui) contra um
 * segredo compartilhado ({@code INTERNAL_API_KEY}) - diferente do {@link JwtService}, que
 * autentica um usuário final. Não há usuário/papel envolvido nessas chamadas: quem decide se
 * uma reserva pode ser feita é o servico-reserva; aqui só interessa confirmar que quem está
 * chamando é de fato um backend interno autorizado, não um cliente direto da API pública.
 */
@Component
public class InternalApiKeyService {
    private final byte[] chaveEsperada;

    public InternalApiKeyService(@Value("${internal.api-key}") String chaveEsperada) {
        this.chaveEsperada = chaveEsperada.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * @param chaveRecebida valor do header {@code X-Internal-Api-Key} da requisição
     * @throws AutenticacaoInternaInvalidaException se ausente ou diferente do segredo configurado
     */
    public void validar(String chaveRecebida) {
        // MessageDigest.isEqual faz comparação em tempo constante - um simples .equals() vazaria,
        // pelo tempo de resposta, quantos caracteres iniciais da chave o chamador acertou.
        if (chaveRecebida == null
                || !MessageDigest.isEqual(chaveRecebida.getBytes(StandardCharsets.UTF_8), chaveEsperada)) {
            throw new AutenticacaoInternaInvalidaException("Chave de API interna ausente ou inválida.");
        }
    }
}

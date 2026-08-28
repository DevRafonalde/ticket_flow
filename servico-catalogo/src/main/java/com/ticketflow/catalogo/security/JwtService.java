package com.ticketflow.catalogo.security;

import com.ticketflow.catalogo.exception.TokenInvalidoException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Valida o JWT emitido pelo servico-autenticacao e extrai a identidade do usuário.
 *
 * <p>Não há chamada HTTP ao servico-autenticacao aqui: o token é um JWT assinado com um
 * segredo HMAC compartilhado entre todos os serviços ({@code JWT_SECRET}, ver .env.example),
 * então qualquer serviço consegue confirmar sozinho que o token foi emitido por quem tem esse
 * segredo (autenticidade) e ainda não expirou (claim {@code exp}) - sem depender da
 * disponibilidade do servico-autenticacao nem pagar uma chamada de rede por requisição.
 * Essa é a mesma premissa descrita em regras-de-negocio.md 1.2 do servico-autenticacao:
 * "o servico-gateway e os demais serviços validam apenas a assinatura e a expiração".
 */
@Component
public class JwtService {
    private static final String CLAIM_PAPEL = "papel";
    private static final String PREFIXO_BEARER = "Bearer ";

    private final SecretKey chaveAssinatura;

    public JwtService(@Value("${jwt.secret}") String segredo) {
        this.chaveAssinatura = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @param cabecalhoAutorizacao valor bruto do header HTTP {@code Authorization}, esperado no
     *                             formato {@code "Bearer <token>"}
     * @return identidade (id + papel) extraída das claims {@code sub} e {@code papel} do token
     * @throws TokenInvalidoException se o header estiver ausente/mal formatado, ou o token tiver
     *                                assinatura inválida, estiver expirado, ou não carregar as
     *                                claims esperadas
     */
    public UsuarioAutenticado autenticar(String cabecalhoAutorizacao) {
        String token = extrairToken(cabecalhoAutorizacao);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chaveAssinatura)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String id = claims.getSubject();
            Papel papel = Papel.valueOf(claims.get(CLAIM_PAPEL, String.class));
            return new UsuarioAutenticado(id, papel);
        } catch (JwtException | IllegalArgumentException e) {
            // Cobre assinatura inválida, token expirado, formato malformado e claim "papel"
            // ausente/desconhecida - todos tratados da mesma forma pelo lado de quem consome
            // a API (401, token inválido), embora a causa interna varie.
            throw new TokenInvalidoException("Token de acesso inválido ou expirado.");
        }
    }

    private String extrairToken(String cabecalhoAutorizacao) {
        if (cabecalhoAutorizacao == null || !cabecalhoAutorizacao.startsWith(PREFIXO_BEARER)) {
            throw new TokenInvalidoException("Header Authorization ausente ou fora do formato esperado ('Bearer <token>').");
        }
        return cabecalhoAutorizacao.substring(PREFIXO_BEARER.length());
    }
}
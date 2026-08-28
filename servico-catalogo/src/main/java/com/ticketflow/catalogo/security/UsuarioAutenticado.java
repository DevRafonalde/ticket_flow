package com.ticketflow.catalogo.security;

/**
 * Identidade extraída de um JWT válido: {@code id} vem da claim {@code sub} (id do usuário
 * no servico-autenticacao) e {@code papel} da claim {@code papel}.
 */
public record UsuarioAutenticado(String id, Papel papel) {
}
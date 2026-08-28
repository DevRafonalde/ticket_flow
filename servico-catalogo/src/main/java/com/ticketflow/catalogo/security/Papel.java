package com.ticketflow.catalogo.security;

/**
 * Espelha os papéis atribuídos pelo servico-autenticacao (claim {@code papel} do JWT).
 * Ver regras-de-negocio.md 1.1/1.4 do servico-autenticacao.
 */
public enum Papel {
    CLIENTE,
    ORGANIZADOR,
    ADMIN
}
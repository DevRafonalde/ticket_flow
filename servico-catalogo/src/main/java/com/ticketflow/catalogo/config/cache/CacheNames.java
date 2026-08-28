package com.ticketflow.catalogo.config.cache;

/**
 * Nomes centralizados dos caches Redis da aplicação.
 * Usados tanto na configuração de cada módulo ({@code @Bean(nome)} de {@code RedisCacheConfiguration})
 * quanto nas anotações {@code @Cacheable} (que exigem valores constantes em tempo de compilação).
 */
public final class CacheNames {
    public static final String TODOS_EVENTOS = "todosEventos";
    public static final String EVENTO_POR_ID = "eventoPorId";
    public static final String DISPONIBILIDADE_EVENTO = "disponibilidadeEvento";

    /**
     * Canal Redis Pub/Sub usado pela API de escrita (que é dona dos dados) para avisar esta
     * API (somente leitura) que algo mudou e o cache precisa ser invalidado. Qualquer mensagem
     * publicada nesse canal limpa TODOS os caches - por acordo com o time da API de escrita,
     * que evicta o cache inteiro a cada mutação (não há granularidade por chave/cache).
     */
    public static final String EVICT_CHANNEL = "servico-catalogo:cache-evict";

    private CacheNames() {
    }
}

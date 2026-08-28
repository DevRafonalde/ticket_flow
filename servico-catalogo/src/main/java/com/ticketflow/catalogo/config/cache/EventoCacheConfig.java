package com.ticketflow.catalogo.config.cache;

import com.ticketflow.catalogo.model.entities.dto.DisponibilidadeEventoDTO;
import com.ticketflow.catalogo.model.entities.dto.EventoDTO;
import com.ticketflow.catalogo.model.entities.dto.PaginaEventos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

/**
 * Configuração de cache Redis dos endpoints do serviço {@code catálogo}. Ver
 * {@link CacheConfig} para o porquê do serializer tipado por cache.
 */
@Configuration
public class EventoCacheConfig {
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final Duration TTL_DISPONIBILIDADE = Duration.ofSeconds(30);

    @Bean(CacheNames.TODOS_EVENTOS)
    public RedisCacheConfiguration todosEventosCacheConfig() {
        return CacheConfig.objectCacheConfiguration(TTL, PaginaEventos.class);
    }

    @Bean(CacheNames.EVENTO_POR_ID)
    public RedisCacheConfiguration eventoPorIdCacheConfig() {
        return CacheConfig.objectCacheConfiguration(TTL, EventoDTO.class);
    }

    @Bean(CacheNames.DISPONIBILIDADE_EVENTO)
    public RedisCacheConfiguration disponibilidadeEventoCacheConfig() {
        return CacheConfig.objectCacheConfiguration(TTL_DISPONIBILIDADE, DisponibilidadeEventoDTO.class);
    }
}

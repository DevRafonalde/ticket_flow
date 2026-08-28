package com.ticketflow.catalogo.config.cache;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * Configuração do cache via Redis (serviço ClusterIP compartilhado no mesmo cluster).
 *
 * Como o Redis é compartilhado com outras aplicações do cluster, as chaves recebem
 * um prefixo próprio ({@code KEY_PREFIX}) para evitar colisão de namespace, e falhas
 * de leitura/escrita no cache (ex: entrada gravada em formato incompatível por outra
 * versão/app) são apenas logadas — nunca devem derrubar a requisição, já que o cache
 * é só uma otimização sobre a consulta ao banco.
 *
 * <p>{@code cacheManager()} coleta automaticamente todo bean {@link RedisCacheConfiguration}
 * presente no contexto - cada cache com serialização tipada é declarado como seu próprio bean,
 * nomeado exatamente como a constante de {@link CacheNames} correspondente (o nome do bean vira
 * o nome do cache), usando {@link #baseCacheConfiguration(Duration)} como ponto de partida.
 * Ver exemplo em {@link EventoCacheConfig}.
 *
 * <p><b>Por que não usar {@link GenericJackson2JsonRedisSerializer} (o default abaixo, usado
 * só como fallback para caches sem bean próprio)?</b> Ele tem uma assimetria entre escrita e
 * leitura quando o valor cacheado é uma {@code List} no nível raiz: na escrita ele infere o tipo
 * a partir da classe concreta do valor e não embrulha a lista com informação de tipo, mas na
 * leitura sempre desserializa contra {@code Object.class} (ambíguo) e por isso espera esse
 * wrapper, falhando com "Unexpected token (START_OBJECT), expected VALUE_STRING". Um
 * {@link Jackson2JsonRedisSerializer} com {@code JavaType} explícito não sofre dessa ambiguidade.
 */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private static final String KEY_PREFIX = "servicoCatalogo::";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                            Map<String, RedisCacheConfiguration> cacheConfigurations) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(baseCacheConfiguration(Duration.ofMinutes(10))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer())))
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * Base para declarar um cache: aplica TTL, prefixo de chave e serializer de chave.
     * Falta só {@code .serializeValuesWith(...)} com o serializer tipado do DTO cacheado.
     */
    public static RedisCacheConfiguration baseCacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> KEY_PREFIX + cacheName + "::")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
    }

    /**
     * Cache de um único objeto do tipo {@code type} (ex: um DTO buscado por id).
     */
    public static RedisCacheConfiguration objectCacheConfiguration(Duration ttl, Class<?> type) {
        JavaType javaType = TypeFactory.defaultInstance().constructType(type);
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, javaType);
        return baseCacheConfiguration(ttl)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new LoggingCacheErrorHandler();
    }

    @Slf4j
    private static class LoggingCacheErrorHandler implements CacheErrorHandler {

        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            log.warn("Falha ao ler cache '{}' (chave={}) - seguindo sem cache", cache.getName(), key, exception);
        }

        @Override
        public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
            log.warn("Falha ao gravar no cache '{}' (chave={}) - seguindo sem cache", cache.getName(), key, exception);
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
            log.warn("Falha ao invalidar cache '{}' (chave={})", cache.getName(), key, exception);
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, Cache cache) {
            log.warn("Falha ao limpar cache '{}'", cache.getName(), exception);
        }
    }
}
package com.ticketflow.catalogo.config.cache;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.util.ErrorHandler;

/**
 * Assina o canal {@link CacheNames#EVICT_CHANNEL} no Redis compartilhado para receber
 * avisos de invalidação de cache publicados pela API de escrita (dona dos dados), já que
 * esta API é somente leitura e não tem como saber por conta própria quando os dados mudaram.
 *
 * <p>O container NÃO é registrado como {@code @Bean} de propósito: {@link RedisMessageListenerContainer}
 * implementa {@code SmartLifecycle} e o Spring o iniciaria automaticamente durante o refresh do
 * contexto - de forma síncrona - derrubando o boot inteiro da aplicação se o Redis estiver
 * indisponível nesse momento. Em vez disso, ele é criado e iniciado manualmente, em background,
 * só depois que a aplicação já está no ar ({@link ApplicationReadyEvent}), com qualquer falha
 * apenas logada: o cache é só uma otimização, nunca pode impedir o boot nem derrubar a aplicação.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class CacheEvictionListenerConfig {

    private final RedisConnectionFactory connectionFactory;
    private final CacheManager cacheManager;

    private volatile RedisMessageListenerContainer container;

    @EventListener(ApplicationReadyEvent.class)
    public void iniciarListenerEmBackground() {
        Thread.ofVirtual()
                .name("cache-eviction-listener-starter")
                .start(this::iniciarListener);
    }

    private void iniciarListener() {
        try {
            RedisMessageListenerContainer novoContainer = new RedisMessageListenerContainer();
            novoContainer.setConnectionFactory(connectionFactory);
            novoContainer.setErrorHandler(errorHandler());
            novoContainer.addMessageListener((message, pattern) -> limparTodosOsCaches(),
                    new ChannelTopic(CacheNames.EVICT_CHANNEL));

            novoContainer.afterPropertiesSet();
            novoContainer.start();

            this.container = novoContainer;
            log.info("Listener de invalidação de cache assinando o canal '{}'", CacheNames.EVICT_CHANNEL);
        } catch (Exception e) {
            log.warn("Não foi possível assinar o canal '{}' de invalidação de cache "
                    + "- eviction manual indisponível, cache seguirá valendo só o TTL",
                    CacheNames.EVICT_CHANNEL, e);
        }
    }

    @PreDestroy
    public void pararListener() {
        if (container != null) {
            container.stop();
        }
    }

    private ErrorHandler errorHandler() {
        return throwable -> log.warn("Erro no listener de invalidação de cache via Redis Pub/Sub "
                + "(canal '{}')", CacheNames.EVICT_CHANNEL, throwable);
    }

    private void limparTodosOsCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        log.info("Todos os caches invalidados via {}", CacheNames.EVICT_CHANNEL);
    }
}
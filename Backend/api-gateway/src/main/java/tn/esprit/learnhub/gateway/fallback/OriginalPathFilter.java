package tn.esprit.learnhub.gateway.fallback;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Mémorise le chemin demandé par le client AVANT tout autre filtre.
 * Quand le circuit est ouvert, la requête part directement vers /fallback/...,
 * et le FallbackController peut quand même indiquer quel endpoint était appelé.
 */
@Component
public class OriginalPathFilter implements GlobalFilter, Ordered {

    public static final String ORIGINAL_PATH_ATTR = "learnhub.originalPath";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().putIfAbsent(ORIGINAL_PATH_ATTR, exchange.getRequest().getPath().value());
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

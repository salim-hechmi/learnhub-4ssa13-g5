package tn.esprit.learnhub.gateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;

/**
 * Réponse de secours du filtre CircuitBreaker (fallbackUri: forward:/fallback/{ms}).
 * Quand le microservice est arrêté, lent ou en erreur, le client reçoit un 503 clair
 * au lieu d'un timeout ou d'une page d'erreur Netty.
 * Toutes les méthodes HTTP sont acceptées : un POST qui échoue est renvoyé ici en POST.
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback/{service}")
    public ResponseEntity<Map<String, Object>> fallback(@PathVariable String service, ServerWebExchange exchange) {
        Throwable cause = exchange.getAttribute(CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", "Service Unavailable");
        body.put("service", service + "-service");
        body.put("message", "Le service " + service + " est momentanément indisponible. Réessayez dans quelques instants.");
        body.put("path", originalPath(exchange));
        body.put("cause", cause == null ? "circuit ouvert" : cause.getClass().getSimpleName());
        body.put("source", "api-gateway (fallback du circuit breaker)");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    /** Chemin demandé par le client (avant la redirection interne vers /fallback/...). */
    private static String originalPath(ServerWebExchange exchange) {
        String saved = exchange.getAttribute(OriginalPathFilter.ORIGINAL_PATH_ATTR);
        if (saved != null) {
            return saved;
        }
        Set<URI> urls = exchange.getAttribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        if (urls != null) {
            for (URI url : urls) {
                if (!url.getPath().startsWith("/fallback/")) {
                    return url.getPath();
                }
            }
        }
        return exchange.getRequest().getPath().value();
    }
}

package tn.esprit.learnhub.marketplace.client;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import tn.esprit.learnhub.marketplace.exception.ExternalServiceUnavailableException;
import tn.esprit.learnhub.marketplace.exception.ResourceNotFoundException;

/**
 * Fallback du circuit breaker.
 * On ne renvoie JAMAIS un faux prix : un achat ne doit pas se faire sur une donnée inventée.
 * - 404 de course-service  -> ResourceNotFoundException (cours inexistant)
 * - panne / timeout / circuit ouvert -> 503 Service Unavailable
 */
@Slf4j
@Component
public class CourseClientFallbackFactory implements FallbackFactory<CourseClient> {

    @Override
    public CourseClient create(Throwable cause) {
        return courseId -> {
            if (cause instanceof FeignException.NotFound) {
                throw new ResourceNotFoundException("Cours introuvable dans le catalogue : id=" + courseId);
            }
            log.warn("Fallback course-service pour courseId={} : {}", courseId, cause.toString());
            throw new ExternalServiceUnavailableException(
                    "Le catalogue des cours (course-service) est momentanément indisponible. Réessayez plus tard.");
        };
    }
}

package tn.esprit.learnhub.marketplace.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.learnhub.marketplace.dto.response.CoursePricingResponse;

/**
 * Appel synchrone vers course-service (découvert via Eureka) pour récupérer
 * l'existence, le titre et le prix RÉEL d'un cours avant de créer la commande.
 * Protégé par un circuit breaker Resilience4j (fallbackFactory).
 */
@FeignClient(name = "course-service", fallbackFactory = CourseClientFallbackFactory.class, primary = false)
public interface CourseClient {

    @GetMapping("/api/courses/{id}/pricing")
    CoursePricingResponse getCoursePricing(@PathVariable("id") Long courseId);
}

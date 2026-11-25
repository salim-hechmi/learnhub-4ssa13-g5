package tn.esprit.learnhub.marketplace.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tn.esprit.learnhub.marketplace.dto.response.CoursePricingResponse;
import tn.esprit.learnhub.marketplace.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.Map;

/**
 * UNIQUEMENT avec le profil "standalone" : catalogue simulé pour tester le Marketplace
 * dans Postman sans démarrer course-service ni Eureka.
 * En profil normal, c'est le vrai client Feign qui est utilisé.
 */
@Slf4j
@Primary
@Component
@Profile("standalone")
public class StandaloneCourseClient implements CourseClient {

    private static final Map<Long, CoursePricingResponse> CATALOG = Map.of(
            1L, new CoursePricingResponse(1L, "Développement web : HTML, CSS et JavaScript", new BigDecimal("59.00"), "PUBLISHED"),
            2L, new CoursePricingResponse(2L, "Python pour débutants", new BigDecimal("45.00"), "PUBLISHED"),
            3L, new CoursePricingResponse(3L, "Bases de données et SQL", new BigDecimal("39.00"), "PUBLISHED"),
            4L, new CoursePricingResponse(4L, "React : créer des interfaces modernes", new BigDecimal("69.00"), "PUBLISHED"),
            5L, new CoursePricingResponse(5L, "Excel pour la gestion de données", new BigDecimal("35.00"), "PUBLISHED"),
            6L, new CoursePricingResponse(6L, "Anglais professionnel (niveau B2)", new BigDecimal("55.00"), "PUBLISHED"),
            7L, new CoursePricingResponse(7L, "Introduction au Machine Learning", new BigDecimal("89.00"), "DRAFT")
    );

    @Override
    public CoursePricingResponse getCoursePricing(Long courseId) {
        log.debug("[standalone] pricing simulé pour courseId={}", courseId);
        CoursePricingResponse course = CATALOG.get(courseId);
        if (course == null) {
            throw new ResourceNotFoundException("Cours introuvable dans le catalogue : id=" + courseId);
        }
        return course;
    }
}

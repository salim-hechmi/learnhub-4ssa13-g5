package tn.esprit.learnhub.marketplace.dto.response;

import java.math.BigDecimal;

/**
 * Contrat de GET /api/courses/{id}/pricing exposé par course-service (Nawres Soua).
 * Champs attendus : id, title, price, status.
 */
public record CoursePricingResponse(
        Long id,
        String title,
        BigDecimal price,
        String status
) {
    public boolean isPurchasable() {
        return status == null || "PUBLISHED".equalsIgnoreCase(status);
    }
}

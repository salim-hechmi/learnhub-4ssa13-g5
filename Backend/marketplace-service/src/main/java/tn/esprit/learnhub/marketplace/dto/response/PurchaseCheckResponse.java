package tn.esprit.learnhub.marketplace.dto.response;

import java.time.LocalDateTime;

/** Réponse Feign pour community-service (badge "Acheteur Vérifié"). */
public record PurchaseCheckResponse(
        String studentId,
        Long courseId,
        boolean purchased,
        Long orderId,
        LocalDateTime paidAt
) {
}

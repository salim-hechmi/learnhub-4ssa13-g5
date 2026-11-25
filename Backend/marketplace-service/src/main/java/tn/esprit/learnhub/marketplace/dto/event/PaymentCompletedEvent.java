package tn.esprit.learnhub.marketplace.dto.event;

import java.time.LocalDateTime;

/**
 * Événement critique publié sur le Topic Exchange dès qu'un paiement passe à SUCCESS.
 * Contrat : { studentId, courseId, orderId, paidAt } — un message par cours acheté.
 * Consommateur : learning-journey-service (inscription automatique).
 */
public record PaymentCompletedEvent(
        String studentId,
        Long courseId,
        Long orderId,
        LocalDateTime paidAt
) {
}

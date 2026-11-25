package tn.esprit.learnhub.marketplace.messaging;

import java.time.LocalDateTime;
import java.util.List;

/** Événement Spring INTERNE, converti en message RabbitMQ seulement APRÈS le commit SQL. */
public record OrderPaidDomainEvent(Long orderId, String studentId, List<Long> courseIds, LocalDateTime paidAt) {
}

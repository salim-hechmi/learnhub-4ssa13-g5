package tn.esprit.learnhub.marketplace.dto.event;

import java.time.LocalDateTime;
import java.util.List;

/** Événement publié lorsqu'une commande est remboursée (routing key order.refunded). */
public record OrderRefundedEvent(
        String studentId,
        Long orderId,
        List<Long> courseIds,
        LocalDateTime refundedAt
) {
}

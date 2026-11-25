package tn.esprit.learnhub.marketplace.dto.response;

import tn.esprit.learnhub.marketplace.enums.PaymentMethod;
import tn.esprit.learnhub.marketplace.enums.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long id,
        Long orderId,
        String studentId,
        BigDecimal amount,
        String currency,
        PaymentMethod method,
        PaymentStatus status,
        String transactionRef
) {
}

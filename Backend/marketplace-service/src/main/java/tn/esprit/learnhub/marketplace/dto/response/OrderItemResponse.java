package tn.esprit.learnhub.marketplace.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long courseId,
        String courseTitle,
        BigDecimal unitPrice
) {
}

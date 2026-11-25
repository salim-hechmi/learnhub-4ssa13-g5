package tn.esprit.learnhub.marketplace.dto.response;

import tn.esprit.learnhub.marketplace.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String studentId,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        int itemsCount,
        List<OrderItemResponse> items,
        List<PaymentResponse> payments,
        Long invoiceId,
        String invoiceNumber
) {
}

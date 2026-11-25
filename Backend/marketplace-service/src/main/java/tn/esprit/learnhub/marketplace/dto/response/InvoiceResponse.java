package tn.esprit.learnhub.marketplace.dto.response;

import tn.esprit.learnhub.marketplace.enums.OrderStatus;
import tn.esprit.learnhub.marketplace.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Facture officielle "imprimable" : en-tête + lignes snapshot + paiement. */
public record InvoiceResponse(
        Long id,
        String invoiceNumber,
        LocalDateTime issuedAt,
        Long orderId,
        String studentId,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime paidAt,
        PaymentMethod paymentMethod,
        String transactionRef,
        /** PAID ou REFUNDED : la facture reste valable mais doit afficher le remboursement. */
        OrderStatus orderStatus
) {
}

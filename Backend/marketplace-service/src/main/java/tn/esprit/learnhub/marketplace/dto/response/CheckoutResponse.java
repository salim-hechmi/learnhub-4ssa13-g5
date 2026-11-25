package tn.esprit.learnhub.marketplace.dto.response;

import tn.esprit.learnhub.marketplace.enums.OrderStatus;
import tn.esprit.learnhub.marketplace.enums.PaymentStatus;

public record CheckoutResponse(
        PaymentStatus paymentStatus,
        OrderStatus orderStatus,
        String message,
        PaymentResponse payment,
        InvoiceResponse invoice
) {
}

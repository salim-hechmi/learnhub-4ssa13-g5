package tn.esprit.learnhub.marketplace.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.learnhub.marketplace.dto.response.InvoiceResponse;
import tn.esprit.learnhub.marketplace.dto.response.OrderItemResponse;
import tn.esprit.learnhub.marketplace.dto.response.OrderResponse;
import tn.esprit.learnhub.marketplace.dto.response.PaymentResponse;
import tn.esprit.learnhub.marketplace.entity.Invoice;
import tn.esprit.learnhub.marketplace.entity.Order;
import tn.esprit.learnhub.marketplace.entity.OrderItem;
import tn.esprit.learnhub.marketplace.entity.Payment;
import tn.esprit.learnhub.marketplace.enums.PaymentStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Conversion Entités JPA -> DTOs de réponse (les entités ne sortent jamais du service). */
@Component
public class MarketplaceMapper {

    public OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(item.getId(), item.getCourseId(), item.getCourseTitle(), item.getUnitPrice());
    }

    public PaymentResponse toPaymentResponse(Payment p) {
        Order o = p.getOrder();
        return new PaymentResponse(
                p.getId(),
                o.getId(),
                o.getStudentId(),
                p.getAmount(),
                o.getCurrency(),
                p.getMethod(),
                p.getStatus(),
                p.getTransactionRef());
    }

    public OrderResponse toOrderResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .sorted(Comparator.comparing(OrderItem::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toItemResponse)
                .toList();
        List<PaymentResponse> payments = o.getPayments().stream().map(this::toPaymentResponse).toList();
        Invoice inv = o.getInvoice();
        return new OrderResponse(
                o.getId(),
                o.getStudentId(),
                o.getStatus(),
                o.getTotalAmount(),
                o.getCurrency(),
                o.getCreatedAt(),
                o.getPaidAt(),
                items.size(),
                items,
                payments,
                inv != null ? inv.getId() : null,
                inv != null ? inv.getInvoiceNumber() : null);
    }

    public InvoiceResponse toInvoiceResponse(Invoice inv) {
        Order o = inv.getOrder();
        Optional<Payment> success = o.getPayments().stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .reduce((first, second) -> second);
        return new InvoiceResponse(
                inv.getId(),
                inv.getInvoiceNumber(),
                inv.getIssuedAt(),
                o.getId(),
                o.getStudentId(),
                o.getItems().stream().map(this::toItemResponse).toList(),
                o.getTotalAmount(),
                o.getCurrency(),
                o.getPaidAt(),
                success.map(Payment::getMethod).orElse(null),
                success.map(Payment::getTransactionRef).orElse(null),
                o.getStatus());
    }
}

package tn.esprit.learnhub.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.learnhub.marketplace.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Commande d'achat (agrégat racine du Bounded Context Marketplace).
 * <p>
 * La table s'appelle "orders" car ORDER est un mot réservé SQL.
 * studentId est une RÉFÉRENCE LOGIQUE (UUID Keycloak) : aucune clé étrangère vers un autre service.
 */
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_student", columnList = "student_id"),
        @Index(name = "idx_orders_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Réf. logique Keycloak (X-User-Id propagé par la Gateway). */
    @Column(name = "student_id", nullable = false, length = 64)
    private String studentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // ----- Relations INTERNES au microservice (même base db_marketplace) -----

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Invoice invoice;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = OrderStatus.PENDING;
    }

    // ----- Méthodes métier (DDD) -----

    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }

    public void clearItems() {
        items.forEach(i -> i.setOrder(null));
        items.clear();
    }

    public void addPayment(Payment payment) {
        payment.setOrder(this);
        payments.add(payment);
    }

    public void attachInvoice(Invoice invoice) {
        invoice.setOrder(this);
        this.invoice = invoice;
    }

    /** Recalcule le total à partir des prix snapshot des lignes. */
    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isPayable() {
        return status == OrderStatus.PENDING || status == OrderStatus.FAILED;
    }
}

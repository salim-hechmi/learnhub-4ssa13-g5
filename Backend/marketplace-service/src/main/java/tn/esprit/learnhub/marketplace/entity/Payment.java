package tn.esprit.learnhub.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.learnhub.marketplace.enums.PaymentMethod;
import tn.esprit.learnhub.marketplace.enums.PaymentStatus;

import java.math.BigDecimal;

/**
 * Tentative de paiement d'une commande (une commande peut avoir plusieurs tentatives :
 * un échec FAILED puis un succès SUCCESS).
 */
@Entity
@Table(name = "payments", indexes = @Index(name = "idx_payments_order", columnList = "order_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_payment_order"))
    private Order order;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "transaction_ref", nullable = false, unique = true, length = 64)
    private String transactionRef;
}

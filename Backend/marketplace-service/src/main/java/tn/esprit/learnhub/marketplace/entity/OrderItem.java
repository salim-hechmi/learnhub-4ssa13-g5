package tn.esprit.learnhub.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Ligne de commande = TABLE D'ASSOCIATION entre une commande (orders) et un cours du catalogue.
 * <p>
 * - orderId  : vraie clé étrangère (même base db_marketplace).
 * - courseId : référence LOGIQUE vers course-service (aucune FK SQL, aucune relation JPA).
 * - courseTitle / unitPrice : SNAPSHOT figé au moment de l'achat (récupéré via OpenFeign).
 */
@Entity
@Table(name = "order_items",
        uniqueConstraints = @UniqueConstraint(name = "uk_order_course", columnNames = {"order_id", "course_id"}),
        indexes = @Index(name = "idx_items_course", columnList = "course_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_item_order"))
    private Order order;

    /** Réf. logique vers course-service. */
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /** Copie snapshot du titre du cours. */
    @Column(name = "course_title", nullable = false)
    private String courseTitle;

    /** Copie snapshot du prix unitaire au moment de l'achat. */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
}

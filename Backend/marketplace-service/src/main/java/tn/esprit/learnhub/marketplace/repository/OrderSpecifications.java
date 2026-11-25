package tn.esprit.learnhub.marketplace.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import tn.esprit.learnhub.marketplace.entity.Order;
import tn.esprit.learnhub.marketplace.entity.OrderItem;
import tn.esprit.learnhub.marketplace.enums.OrderStatus;

import jakarta.persistence.criteria.Subquery;
import java.time.LocalDate;

/** Filtres dynamiques du backoffice (statut, étudiant, cours, période, recherche libre). */
public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, q, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> hasStudent(String studentId) {
        return (root, q, cb) -> !StringUtils.hasText(studentId) ? null : cb.equal(root.get("studentId"), studentId);
    }

    public static Specification<Order> createdFrom(LocalDate from) {
        return (root, q, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay());
    }

    public static Specification<Order> createdTo(LocalDate to) {
        return (root, q, cb) -> to == null ? null : cb.lessThan(root.get("createdAt"), to.plusDays(1).atStartOfDay());
    }

    /** Commandes contenant un cours donné (sous-requête pour éviter les doublons de pagination). */
    public static Specification<Order> containsCourse(Long courseId) {
        return (root, query, cb) -> {
            if (courseId == null) return null;
            Subquery<Long> sub = query.subquery(Long.class);
            var item = sub.from(OrderItem.class);
            sub.select(item.get("order").get("id"))
                    .where(cb.equal(item.get("courseId"), courseId));
            return root.get("id").in(sub);
        };
    }

    /** Recherche libre : n° de commande, studentId ou titre de cours (snapshot). */
    public static Specification<Order> matches(String term) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(term)) return null;
            String like = "%" + term.trim().toLowerCase() + "%";
            Subquery<Long> sub = query.subquery(Long.class);
            var item = sub.from(OrderItem.class);
            sub.select(item.get("order").get("id"))
                    .where(cb.like(cb.lower(item.get("courseTitle")), like));
            var byTitle = root.get("id").in(sub);
            var byStudent = cb.like(cb.lower(root.get("studentId")), like);
            String digits = term.replaceAll("[^0-9]", "");
            if (!digits.isEmpty() && digits.length() < 18) {
                return cb.or(byTitle, byStudent, cb.equal(root.get("id"), Long.parseLong(digits)));
            }
            return cb.or(byTitle, byStudent);
        };
    }
}

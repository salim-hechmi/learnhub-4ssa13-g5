package tn.esprit.learnhub.marketplace.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.learnhub.marketplace.entity.OrderItem;
import tn.esprit.learnhub.marketplace.enums.OrderStatus;

import java.util.Collection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /** Cours (parmi courseIds) déjà achetés et payés par l'étudiant -> empêche le double achat. */
    @Query("""
            select distinct oi.courseId from OrderItem oi
            where oi.order.studentId = :studentId
              and oi.order.status = :status
              and oi.courseId in :courseIds
            """)
    List<Long> findCourseIdsByStudentAndStatus(@Param("studentId") String studentId,
                                               @Param("status") OrderStatus status,
                                               @Param("courseIds") Collection<Long> courseIds);

    /** Lignes payées d'un étudiant pour un cours (vérification "Acheteur Vérifié"). */
    @Query("""
            select oi from OrderItem oi join fetch oi.order o
            where o.studentId = :studentId and oi.courseId = :courseId and o.status = :status
            order by o.paidAt desc
            """)
    List<OrderItem> findPurchases(@Param("studentId") String studentId,
                                  @Param("courseId") Long courseId,
                                  @Param("status") OrderStatus status);

    /** Top des cours vendus : [courseId, courseTitle, salesCount, revenue]. */
    @Query("""
            select oi.courseId, max(oi.courseTitle), count(oi), sum(oi.unitPrice)
            from OrderItem oi
            where oi.order.status = :status
            group by oi.courseId
            order by count(oi) desc, sum(oi.unitPrice) desc
            """)
    List<Object[]> findTopCourses(@Param("status") OrderStatus status, Pageable pageable);
}

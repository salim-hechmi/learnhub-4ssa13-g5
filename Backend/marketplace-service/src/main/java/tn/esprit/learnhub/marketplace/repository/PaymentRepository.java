package tn.esprit.learnhub.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.learnhub.marketplace.entity.Payment;
import tn.esprit.learnhub.marketplace.enums.PaymentMethod;
import tn.esprit.learnhub.marketplace.enums.PaymentStatus;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrder_IdOrderByIdAsc(Long orderId);

    long countByStatus(PaymentStatus status);

    @Query("""
            select p from Payment p
            where (:status is null or p.status = :status)
              and (:method is null or p.method = :method)
            """)
    Page<Payment> search(@Param("status") PaymentStatus status,
                         @Param("method") PaymentMethod method,
                         Pageable pageable);
}

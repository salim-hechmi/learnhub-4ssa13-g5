package tn.esprit.learnhub.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.learnhub.marketplace.entity.Invoice;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByOrder_Id(Long orderId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}

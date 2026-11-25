package tn.esprit.learnhub.marketplace.service;

import org.springframework.data.domain.Pageable;
import tn.esprit.learnhub.marketplace.dto.response.InvoiceResponse;
import tn.esprit.learnhub.marketplace.dto.response.PageResponse;
import tn.esprit.learnhub.marketplace.entity.Invoice;
import tn.esprit.learnhub.marketplace.entity.Order;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

public interface InvoiceService {

    /** Génère (une seule fois) la facture d'une commande qui vient d'être payée. */
    Invoice issueInvoiceFor(Order order);

    InvoiceResponse getInvoice(Long id, CurrentUser user);

    InvoiceResponse getInvoiceByOrder(Long orderId, CurrentUser user);

    InvoiceResponse getInvoiceByNumber(String invoiceNumber);

    PageResponse<InvoiceResponse> getInvoices(Pageable pageable);
}

package tn.esprit.learnhub.marketplace.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.learnhub.marketplace.dto.response.InvoiceResponse;
import tn.esprit.learnhub.marketplace.dto.response.PageResponse;
import tn.esprit.learnhub.marketplace.entity.Invoice;
import tn.esprit.learnhub.marketplace.entity.Order;
import tn.esprit.learnhub.marketplace.exception.ResourceNotFoundException;
import tn.esprit.learnhub.marketplace.mapper.MarketplaceMapper;
import tn.esprit.learnhub.marketplace.repository.InvoiceRepository;
import tn.esprit.learnhub.marketplace.service.InvoiceService;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final MarketplaceMapper mapper;

    /**
     * Numéro de facture lisible et unique : INV-{année}-{id commande sur 6 chiffres}
     * ex. INV-2026-000042. Unicité garantie car une commande n'a qu'une seule facture.
     */
    @Override
    public Invoice issueInvoiceFor(Order order) {
        if (order.getInvoice() != null) {
            return order.getInvoice(); // idempotent
        }
        LocalDateTime issuedAt = order.getPaidAt() != null ? order.getPaidAt() : LocalDateTime.now();
        Invoice invoice = Invoice.builder()
                .invoiceNumber(String.format("INV-%d-%06d", issuedAt.getYear(), order.getId()))
                .issuedAt(issuedAt)
                .build();
        order.attachInvoice(invoice);
        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(Long id, CurrentUser user) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable : id=" + id));
        AccessGuard.checkOwnerOrAdmin(invoice.getOrder(), user);
        return mapper.toInvoiceResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByOrder(Long orderId, CurrentUser user) {
        Invoice invoice = invoiceRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucune facture pour la commande id=" + orderId + " (commande non payée ?)"));
        AccessGuard.checkOwnerOrAdmin(invoice.getOrder(), user);
        return mapper.toInvoiceResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .map(mapper::toInvoiceResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable : " + invoiceNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InvoiceResponse> getInvoices(Pageable pageable) {
        return PageResponse.of(invoiceRepository.findAll(pageable), mapper::toInvoiceResponse);
    }
}

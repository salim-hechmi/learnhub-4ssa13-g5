package tn.esprit.learnhub.marketplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tn.esprit.learnhub.marketplace.dto.response.InvoiceResponse;
import tn.esprit.learnhub.marketplace.dto.response.PageResponse;
import tn.esprit.learnhub.marketplace.service.InvoiceService;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

@Validated
@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "3. Factures (Invoices)", description = "Factures officielles générées après paiement")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer la facture officielle liée à un achat")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable @Positive Long id, CurrentUser user) {
        return ResponseEntity.ok(invoiceService.getInvoice(id, user));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Facture d'une commande")
    public ResponseEntity<InvoiceResponse> getByOrder(@PathVariable @Positive Long orderId, CurrentUser user) {
        return ResponseEntity.ok(invoiceService.getInvoiceByOrder(orderId, user));
    }

    @GetMapping("/number/{invoiceNumber}")
    @Operation(summary = "[ADMIN] Rechercher une facture par son numéro (ex. INV-2026-000001)")
    public ResponseEntity<InvoiceResponse> getByNumber(@PathVariable String invoiceNumber, CurrentUser user) {
        AdminGuard.requireAdmin(user);
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(invoiceNumber));
    }

    @GetMapping
    @Operation(summary = "[ADMIN] Registre paginé des factures")
    public ResponseEntity<PageResponse<InvoiceResponse>> list(
            @ParameterObject @PageableDefault(size = 10, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable,
            CurrentUser user) {
        AdminGuard.requireAdmin(user);
        return ResponseEntity.ok(invoiceService.getInvoices(pageable));
    }
}

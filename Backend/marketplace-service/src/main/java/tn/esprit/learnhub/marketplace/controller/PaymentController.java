package tn.esprit.learnhub.marketplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tn.esprit.learnhub.marketplace.dto.request.CheckoutRequest;
import tn.esprit.learnhub.marketplace.dto.response.CheckoutResponse;
import tn.esprit.learnhub.marketplace.dto.response.PageResponse;
import tn.esprit.learnhub.marketplace.dto.response.PaymentResponse;
import tn.esprit.learnhub.marketplace.enums.PaymentMethod;
import tn.esprit.learnhub.marketplace.enums.PaymentStatus;
import tn.esprit.learnhub.marketplace.service.PaymentService;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "2. Paiements (Payments)", description = "Passerelle sandbox simulée Stripe / PayPal / Simulateur")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    @Operation(summary = "Payer une commande (sandbox)",
            description = "SUCCESS -> commande PAID, facture émise, événement PaymentCompleted publié sur RabbitMQ. "
                    + "Carte se terminant par 0002 ou simulateFailure=true -> paiement refusé (402).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement accepté"),
            @ApiResponse(responseCode = "402", description = "Paiement refusé (tentative FAILED enregistrée)"),
            @ApiResponse(responseCode = "404", description = "Commande introuvable"),
            @ApiResponse(responseCode = "409", description = "Commande déjà payée ou remboursée")
    })
    public ResponseEntity<CheckoutResponse> checkout(@Valid @RequestBody CheckoutRequest request, CurrentUser user) {
        CheckoutResponse response = paymentService.checkout(request, user);
        HttpStatus status = response.paymentStatus() == PaymentStatus.SUCCESS ? HttpStatus.OK : HttpStatus.PAYMENT_REQUIRED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping
    @Operation(summary = "[ADMIN] Journal paginé des transactions (filtres status, method)")
    public ResponseEntity<PageResponse<PaymentResponse>> list(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) PaymentMethod method,
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            CurrentUser user) {
        AdminGuard.requireAdmin(user);
        return ResponseEntity.ok(paymentService.getPayments(status, method, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "[ADMIN] Détail d'une transaction")
    public ResponseEntity<PaymentResponse> getById(@PathVariable @Positive Long id, CurrentUser user) {
        AdminGuard.requireAdmin(user);
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Tentatives de paiement d'une commande")
    public ResponseEntity<List<PaymentResponse>> byOrder(@PathVariable @Positive Long orderId, CurrentUser user) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrder(orderId, user));
    }
}

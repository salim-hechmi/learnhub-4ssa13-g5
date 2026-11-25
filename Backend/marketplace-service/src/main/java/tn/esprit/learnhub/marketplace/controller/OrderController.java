package tn.esprit.learnhub.marketplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tn.esprit.learnhub.marketplace.dto.request.CreateOrderRequest;
import tn.esprit.learnhub.marketplace.dto.request.UpdateOrderRequest;
import tn.esprit.learnhub.marketplace.dto.request.UpdateOrderStatusRequest;
import tn.esprit.learnhub.marketplace.dto.response.MarketplaceStatsResponse;
import tn.esprit.learnhub.marketplace.dto.response.OrderResponse;
import tn.esprit.learnhub.marketplace.dto.response.PageResponse;
import tn.esprit.learnhub.marketplace.dto.response.PurchaseCheckResponse;
import tn.esprit.learnhub.marketplace.enums.OrderStatus;
import tn.esprit.learnhub.marketplace.service.OrderService;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "1. Commandes (Orders)", description = "CRUD des commandes d'achat de cours + backoffice ADMIN")
public class OrderController {

    private final OrderService orderService;

    // ------------------------------ CREATE ------------------------------
    @PostMapping
    @Operation(summary = "Créer une commande",
            description = "Vérifie chaque cours via OpenFeign (course-service /pricing) et copie titre + prix en snapshot.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Commande créée (PENDING)"),
            @ApiResponse(responseCode = "400", description = "Validation échouée / cours non publié"),
            @ApiResponse(responseCode = "404", description = "Cours inexistant"),
            @ApiResponse(responseCode = "409", description = "Cours déjà acheté"),
            @ApiResponse(responseCode = "503", description = "course-service indisponible (circuit breaker)")
    })
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request, CurrentUser user) {
        OrderResponse created = orderService.createOrder(request, user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    // ------------------------------ READ ------------------------------
    @GetMapping("/my")
    @Operation(summary = "Historique des commandes de l'étudiant connecté")
    public ResponseEntity<List<OrderResponse>> myOrders(CurrentUser user) {
        return ResponseEntity.ok(orderService.getMyOrders(user));
    }

    @GetMapping
    @Operation(summary = "[ADMIN] Liste paginée et filtrable de toutes les commandes",
            description = "Filtres : status, studentId, courseId, q (n° commande / étudiant / titre), from, to (yyyy-MM-dd). "
                    + "Pagination : page, size, sort=createdAt,desc")
    public ResponseEntity<PageResponse<OrderResponse>> search(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            CurrentUser user) {
        AdminGuard.requireAdmin(user);
        return ResponseEntity.ok(orderService.searchOrders(status, studentId, courseId, q, from, to, pageable));
    }

    @GetMapping("/stats")
    @Operation(summary = "[ADMIN] Indicateurs du tableau de bord (CA, taux de succès, top cours)")
    public ResponseEntity<MarketplaceStatsResponse> stats(CurrentUser user) {
        AdminGuard.requireAdmin(user);
        return ResponseEntity.ok(orderService.getStats());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une commande (lignes, paiements, facture)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Commande d'un autre étudiant"),
            @ApiResponse(responseCode = "404", description = "Commande introuvable")
    })
    public ResponseEntity<OrderResponse> getById(@PathVariable @Positive Long id, CurrentUser user) {
        return ResponseEntity.ok(orderService.getOrderById(id, user));
    }

    @GetMapping("/check-purchase")
    @Operation(summary = "[Feign interne] L'étudiant a-t-il acheté ce cours ?",
            description = "Utilisé par community-service pour apposer le badge « Acheteur Vérifié » sur un avis.")
    public ResponseEntity<PurchaseCheckResponse> checkPurchase(
            @Parameter(example = "a1b2c3d4-0000-4000-8000-000000000001") @RequestParam @NotBlank String studentId,
            @Parameter(example = "1") @RequestParam @Positive Long courseId) {
        return ResponseEntity.ok(orderService.checkPurchase(studentId, courseId));
    }

    // ------------------------------ UPDATE ------------------------------
    @PutMapping("/{id}")
    @Operation(summary = "Modifier le contenu d'une commande (PENDING / FAILED uniquement)")
    public ResponseEntity<OrderResponse> update(@PathVariable @Positive Long id,
                                                @Valid @RequestBody UpdateOrderRequest request,
                                                CurrentUser user) {
        return ResponseEntity.ok(orderService.updateOrder(id, request, user));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "[ADMIN] Changer le statut (PENDING ↔ FAILED, PAID → REFUNDED)")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable @Positive Long id,
                                                      @Valid @RequestBody UpdateOrderStatusRequest request,
                                                      CurrentUser user) {
        AdminGuard.requireAdmin(user);
        return ResponseEntity.ok(orderService.updateStatus(id, request.status()));
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "[ADMIN] Rembourser une commande payée (publie OrderRefunded)")
    public ResponseEntity<OrderResponse> refund(@PathVariable @Positive Long id, CurrentUser user) {
        AdminGuard.requireAdmin(user);
        return ResponseEntity.ok(orderService.refundOrder(id));
    }

    // ------------------------------ DELETE ------------------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une commande non payée (PENDING / FAILED)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supprimée"),
            @ApiResponse(responseCode = "409", description = "Commande payée/remboursée : suppression interdite")
    })
    public void delete(@PathVariable @Positive Long id, CurrentUser user) {
        orderService.deleteOrder(id, user);
    }
}

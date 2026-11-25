package tn.esprit.learnhub.marketplace.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tn.esprit.learnhub.marketplace.client.CourseClient;
import tn.esprit.learnhub.marketplace.dto.event.OrderRefundedEvent;
import tn.esprit.learnhub.marketplace.dto.request.CreateOrderRequest;
import tn.esprit.learnhub.marketplace.dto.request.UpdateOrderRequest;
import tn.esprit.learnhub.marketplace.dto.response.CoursePricingResponse;
import tn.esprit.learnhub.marketplace.dto.response.MarketplaceStatsResponse;
import tn.esprit.learnhub.marketplace.dto.response.OrderResponse;
import tn.esprit.learnhub.marketplace.dto.response.PageResponse;
import tn.esprit.learnhub.marketplace.dto.response.PurchaseCheckResponse;
import tn.esprit.learnhub.marketplace.entity.Order;
import tn.esprit.learnhub.marketplace.entity.OrderItem;
import tn.esprit.learnhub.marketplace.enums.OrderStatus;
import tn.esprit.learnhub.marketplace.enums.PaymentStatus;
import tn.esprit.learnhub.marketplace.exception.BusinessException;
import tn.esprit.learnhub.marketplace.exception.ConflictException;
import tn.esprit.learnhub.marketplace.exception.ResourceNotFoundException;
import tn.esprit.learnhub.marketplace.mapper.MarketplaceMapper;
import tn.esprit.learnhub.marketplace.repository.InvoiceRepository;
import tn.esprit.learnhub.marketplace.repository.OrderItemRepository;
import tn.esprit.learnhub.marketplace.repository.OrderRepository;
import tn.esprit.learnhub.marketplace.repository.OrderSpecifications;
import tn.esprit.learnhub.marketplace.repository.PaymentRepository;
import tn.esprit.learnhub.marketplace.service.OrderService;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final CourseClient courseClient;
    private final MarketplaceMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${marketplace.currency:TND}")
    private String defaultCurrency;

    // =====================================================================
    // CREATE
    // =====================================================================
    @Override
    public OrderResponse createOrder(CreateOrderRequest request, CurrentUser user) {
        String studentId = resolveStudentId(request.studentId(), user);
        Set<Long> courseIds = new LinkedHashSet<>(request.courseIds()); // supprime les doublons, garde l'ordre

        ensureNotAlreadyPurchased(studentId, courseIds);

        Order order = Order.builder()
                .studentId(studentId)
                .status(OrderStatus.PENDING)
                .currency(resolveCurrency(request.currency()))
                .createdAt(LocalDateTime.now())
                .build();

        // Appel OpenFeign -> course-service : prix et titre RÉELS, copiés en snapshot
        for (Long courseId : courseIds) {
            CoursePricingResponse pricing = fetchPurchasableCourse(courseId);
            order.addItem(OrderItem.builder()
                    .courseId(courseId)
                    .courseTitle(pricing.title())
                    .unitPrice(pricing.price())
                    .build());
        }
        order.recalculateTotal();

        Order saved = orderRepository.save(order);
        log.info("Commande #{} créée pour {} : {} cours, total {} {}",
                saved.getId(), studentId, courseIds.size(), saved.getTotalAmount(), saved.getCurrency());
        return mapper.toOrderResponse(saved);
    }

    // =====================================================================
    // READ
    // =====================================================================
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(CurrentUser user) {
        return orderRepository.findByStudentIdOrderByCreatedAtDesc(user.id()).stream()
                .map(mapper::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id, CurrentUser user) {
        Order order = findOrder(id);
        AccessGuard.checkOwnerOrAdmin(order, user);
        return mapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> searchOrders(OrderStatus status, String studentId, Long courseId, String q,
                                                    LocalDate from, LocalDate to, Pageable pageable) {
        Specification<Order> spec = Specification.where(OrderSpecifications.hasStatus(status))
                .and(OrderSpecifications.hasStudent(studentId))
                .and(OrderSpecifications.containsCourse(courseId))
                .and(OrderSpecifications.matches(q))
                .and(OrderSpecifications.createdFrom(from))
                .and(OrderSpecifications.createdTo(to));
        return PageResponse.of(orderRepository.findAll(spec, pageable), mapper::toOrderResponse);
    }

    // =====================================================================
    // UPDATE
    // =====================================================================
    @Override
    public OrderResponse updateOrder(Long id, UpdateOrderRequest request, CurrentUser user) {
        Order order = findOrder(id);
        AccessGuard.checkOwnerOrAdmin(order, user);
        if (!order.isPayable()) {
            throw new ConflictException("La commande #" + id + " est " + order.getStatus().label()
                    + " : seules les commandes en attente ou échouées sont modifiables");
        }

        Set<Long> wanted = new LinkedHashSet<>(request.courseIds());
        ensureNotAlreadyPurchased(order.getStudentId(), wanted);

        // On réutilise les lignes existantes (évite la violation de uk_order_course au flush)
        Map<Long, OrderItem> existing = order.getItems().stream()
                .collect(Collectors.toMap(OrderItem::getCourseId, Function.identity()));
        order.getItems().removeIf(item -> !wanted.contains(item.getCourseId()));

        for (Long courseId : wanted) {
            CoursePricingResponse pricing = fetchPurchasableCourse(courseId); // snapshot rafraîchi
            OrderItem item = existing.get(courseId);
            if (item != null) {
                item.setCourseTitle(pricing.title());
                item.setUnitPrice(pricing.price());
            } else {
                order.addItem(OrderItem.builder()
                        .courseId(courseId)
                        .courseTitle(pricing.title())
                        .unitPrice(pricing.price())
                        .build());
            }
        }
        order.setCurrency(resolveCurrency(request.currency()));
        order.recalculateTotal();
        if (order.getStatus() == OrderStatus.FAILED) {
            order.setStatus(OrderStatus.PENDING); // nouvelle tentative possible
        }
        orderRepository.flush(); // la commande est déjà gérée : flush persiste les nouvelles lignes en place
        return mapper.toOrderResponse(order);
    }

    /**
     * Changement de statut manuel (backoffice). Les transitions vers PAID sont interdites ici :
     * un paiement passe OBLIGATOIREMENT par /api/payments/checkout (facture + événement).
     */
    @Override
    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {
        Order order = findOrder(id);
        OrderStatus current = order.getStatus();
        if (current == newStatus) {
            throw new BusinessException("La commande #" + id + " est déjà " + newStatus.label());
        }
        if (newStatus == OrderStatus.REFUNDED) {
            return refundOrder(id);
        }
        boolean allowed = switch (current) {
            case PENDING -> newStatus == OrderStatus.FAILED;
            case FAILED -> newStatus == OrderStatus.PENDING;
            case PAID, REFUNDED -> false;
        };
        if (!allowed) {
            throw new ConflictException(newStatus == OrderStatus.PAID
                    ? "Une commande ne peut passer à « payée » qu'après un paiement accepté"
                    : "Impossible de passer une commande " + current.label() + " à « " + newStatus.label() + " »");
        }
        order.setStatus(newStatus);
        return mapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse refundOrder(Long id) {
        Order order = findOrder(id);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new ConflictException("Seule une commande payée peut être remboursée (commande #" + id
                    + " " + order.getStatus().label() + ")");
        }
        order.setStatus(OrderStatus.REFUNDED);
        eventPublisher.publishEvent(new OrderRefundedEvent(
                order.getStudentId(),
                order.getId(),
                order.getItems().stream().map(OrderItem::getCourseId).toList(),
                LocalDateTime.now()));
        log.info("Commande #{} remboursée", id);
        return mapper.toOrderResponse(order);
    }

    // =====================================================================
    // DELETE
    // =====================================================================
    @Override
    public void deleteOrder(Long id, CurrentUser user) {
        Order order = findOrder(id);
        AccessGuard.checkOwnerOrAdmin(order, user);
        if (!order.isPayable()) {
            throw new ConflictException("La commande #" + id + " est " + order.getStatus().label()
                    + " : elle a une facture et ne peut pas être supprimée"
                    + (order.getStatus() == OrderStatus.PAID ? " (utilisez le remboursement)" : ""));
        }
        orderRepository.delete(order); // cascade : order_items + payments FAILED
        log.info("Commande #{} supprimée", id);
    }

    // =====================================================================
    // FEIGN (community-service) & STATISTIQUES (backoffice)
    // =====================================================================
    @Override
    @Transactional(readOnly = true)
    public PurchaseCheckResponse checkPurchase(String studentId, Long courseId) {
        return orderItemRepository.findPurchases(studentId, courseId, OrderStatus.PAID).stream()
                .findFirst()
                .map(item -> new PurchaseCheckResponse(studentId, courseId, true,
                        item.getOrder().getId(), item.getOrder().getPaidAt()))
                .orElse(new PurchaseCheckResponse(studentId, courseId, false, null, null));
    }

    @Override
    @Transactional(readOnly = true)
    public MarketplaceStatsResponse getStats() {
        long total = orderRepository.count();
        long pending = orderRepository.countByStatus(OrderStatus.PENDING);
        long paid = orderRepository.countByStatus(OrderStatus.PAID);
        long failed = orderRepository.countByStatus(OrderStatus.FAILED);
        long refunded = orderRepository.countByStatus(OrderStatus.REFUNDED);
        BigDecimal revenue = orderRepository.sumTotalAmountByStatus(OrderStatus.PAID);
        BigDecimal avg = paid == 0 ? BigDecimal.ZERO
                : revenue.divide(BigDecimal.valueOf(paid), 2, RoundingMode.HALF_UP);

        long payments = paymentRepository.count();
        long success = paymentRepository.countByStatus(PaymentStatus.SUCCESS);
        double rate = payments == 0 ? 0.0 : Math.round(success * 1000.0 / payments) / 10.0;

        List<MarketplaceStatsResponse.TopCourse> top = new ArrayList<>();
        for (Object[] row : orderItemRepository.findTopCourses(OrderStatus.PAID, PageRequest.of(0, 5))) {
            top.add(new MarketplaceStatsResponse.TopCourse(
                    (Long) row[0], (String) row[1], ((Number) row[2]).longValue(), (BigDecimal) row[3]));
        }

        return new MarketplaceStatsResponse(total, pending, paid, failed, refunded,
                revenue, avg, defaultCurrency, payments, success, rate, invoiceRepository.count(), top);
    }

    // =====================================================================
    // Helpers
    // =====================================================================
    private Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable : id=" + id));
    }

    /** STUDENT : toujours lui-même. ADMIN : peut cibler un étudiant (backoffice). */
    private String resolveStudentId(String requestedStudentId, CurrentUser user) {
        if (user.isAdmin() && StringUtils.hasText(requestedStudentId)) {
            return requestedStudentId.trim();
        }
        return user.id();
    }

    private void ensureNotAlreadyPurchased(String studentId, Set<Long> courseIds) {
        List<Long> owned = orderItemRepository.findCourseIdsByStudentAndStatus(studentId, OrderStatus.PAID, courseIds);
        if (!owned.isEmpty()) {
            throw new ConflictException("Cours déjà acheté(s) par cet étudiant : " + owned);
        }
    }

    /**
     * Les prix viennent du catalogue dans la devise de la plateforme : aucune conversion n'est faite,
     * donc une autre devise afficherait un montant faux (49 TND facturés « 49 EUR »).
     */
    private String resolveCurrency(String requested) {
        if (!StringUtils.hasText(requested)) {
            return defaultCurrency;
        }
        if (!requested.equals(defaultCurrency)) {
            throw new BusinessException("Devise non prise en charge : " + requested
                    + ". Les cours sont vendus en " + defaultCurrency);
        }
        return requested;
    }

    private CoursePricingResponse fetchPurchasableCourse(Long courseId) {
        CoursePricingResponse pricing = courseClient.getCoursePricing(courseId);
        if (pricing == null || pricing.price() == null) {
            throw new BusinessException("Prix indisponible pour le cours id=" + courseId);
        }
        if (!pricing.isPurchasable()) {
            throw new BusinessException("Le cours « " + pricing.title() + " » n'est pas encore publié : achat impossible");
        }
        if (pricing.price().signum() < 0) {
            throw new BusinessException("Prix invalide pour le cours id=" + courseId);
        }
        return pricing;
    }
}

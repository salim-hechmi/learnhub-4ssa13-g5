package tn.esprit.learnhub.marketplace.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.learnhub.marketplace.dto.request.CheckoutRequest;
import tn.esprit.learnhub.marketplace.dto.response.CheckoutResponse;
import tn.esprit.learnhub.marketplace.dto.response.InvoiceResponse;
import tn.esprit.learnhub.marketplace.dto.response.PageResponse;
import tn.esprit.learnhub.marketplace.dto.response.PaymentResponse;
import tn.esprit.learnhub.marketplace.entity.Invoice;
import tn.esprit.learnhub.marketplace.entity.Order;
import tn.esprit.learnhub.marketplace.entity.OrderItem;
import tn.esprit.learnhub.marketplace.entity.Payment;
import tn.esprit.learnhub.marketplace.enums.OrderStatus;
import tn.esprit.learnhub.marketplace.enums.PaymentMethod;
import tn.esprit.learnhub.marketplace.enums.PaymentStatus;
import tn.esprit.learnhub.marketplace.exception.BusinessException;
import tn.esprit.learnhub.marketplace.exception.ConflictException;
import tn.esprit.learnhub.marketplace.exception.ResourceNotFoundException;
import tn.esprit.learnhub.marketplace.mapper.MarketplaceMapper;
import tn.esprit.learnhub.marketplace.messaging.OrderPaidDomainEvent;
import tn.esprit.learnhub.marketplace.repository.OrderItemRepository;
import tn.esprit.learnhub.marketplace.repository.OrderRepository;
import tn.esprit.learnhub.marketplace.repository.PaymentRepository;
import tn.esprit.learnhub.marketplace.service.InvoiceService;
import tn.esprit.learnhub.marketplace.service.PaymentService;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Passerelle de paiement SANDBOX simulée (aucun appel réel à Stripe / PayPal).
 * <p>
 * Règles de simulation (inspirées des cartes de test Stripe) :
 * <ul>
 *   <li>simulateFailure = true            -> refus</li>
 *   <li>carte se terminant par 0002       -> refus "carte déclinée"</li>
 *   <li>carte se terminant par 9995       -> refus "fonds insuffisants"</li>
 *   <li>tout le reste                     -> SUCCESS</li>
 * </ul>
 * En cas de SUCCESS : commande PAID + facture + événement PaymentCompleted (après commit).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final String ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter SIM_TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceService invoiceService;
    private final MarketplaceMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public CheckoutResponse checkout(CheckoutRequest request, CurrentUser user) {
        Order order = orderRepository.findByIdForUpdate(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable : id=" + request.orderId()));
        AccessGuard.checkOwnerOrAdmin(order, user);

        if (order.getStatus() == OrderStatus.PAID) {
            throw new ConflictException("La commande #" + order.getId() + " est déjà payée");
        }
        if (order.getStatus() == OrderStatus.REFUNDED) {
            throw new ConflictException("La commande #" + order.getId() + " a été remboursée : créez une nouvelle commande");
        }
        if (request.method() == PaymentMethod.STRIPE && request.cardNumber() == null) {
            throw new BusinessException("Le numéro de carte est obligatoire pour un paiement par carte");
        }
        if (order.getItems().isEmpty()) {
            throw new BusinessException("Impossible de payer une commande vide");
        }
        // Un même cours peut se trouver dans deux commandes en attente : si l'une a déjà été payée,
        // payer l'autre ferait acheter le cours deux fois.
        List<Long> courseIds = order.getItems().stream().map(OrderItem::getCourseId).toList();
        List<Long> owned = orderItemRepository.findCourseIdsByStudentAndStatus(order.getStudentId(), OrderStatus.PAID, courseIds);
        if (!owned.isEmpty()) {
            throw new ConflictException("Cours déjà acheté(s) dans une autre commande : " + owned
                    + ". Retirez-les de la commande #" + order.getId() + " avant de payer");
        }

        String declineReason = simulateGateway(request);
        boolean success = declineReason == null;

        Payment payment = Payment.builder()
                .amount(order.getTotalAmount())
                .method(request.method())
                .status(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .transactionRef(generateTransactionRef(request.method()))
                .build();
        order.addPayment(payment);
        payment = paymentRepository.save(payment); // persist explicite : l'id est attribué sur CET objet

        InvoiceResponse invoiceResponse = null;
        String message;

        if (success) {
            LocalDateTime now = LocalDateTime.now();
            order.setStatus(OrderStatus.PAID);
            order.setPaidAt(now);
            orderRepository.flush();
            Invoice invoice = invoiceService.issueInvoiceFor(order);
            invoiceResponse = mapper.toInvoiceResponse(invoice);

            // Événement interne -> publié sur RabbitMQ APRÈS le commit (voir MarketplaceEventPublisher)
            eventPublisher.publishEvent(new OrderPaidDomainEvent(
                    order.getId(),
                    order.getStudentId(),
                    order.getItems().stream().map(OrderItem::getCourseId).toList(),
                    now));

            message = "Paiement accepté. Facture " + invoice.getInvoiceNumber()
                    + " émise, l'accès aux cours est en cours d'activation.";
            log.info("Paiement accepté commande #{} ({} {}) ref={}",
                    order.getId(), order.getTotalAmount(), order.getCurrency(), payment.getTransactionRef());
        } else {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.flush();
            message = "Paiement refusé : " + declineReason + ". Vous pouvez réessayer.";
            log.warn("Paiement refusé commande #{} : {}", order.getId(), declineReason);
        }

        return new CheckoutResponse(payment.getStatus(), order.getStatus(), message,
                mapper.toPaymentResponse(payment), invoiceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPayments(PaymentStatus status, PaymentMethod method, Pageable pageable) {
        return PageResponse.of(paymentRepository.search(status, method, pageable), mapper::toPaymentResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id) {
        return paymentRepository.findById(id)
                .map(mapper::toPaymentResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement introuvable : id=" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrder(Long orderId, CurrentUser user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable : id=" + orderId));
        AccessGuard.checkOwnerOrAdmin(order, user);
        return paymentRepository.findByOrder_IdOrderByIdAsc(orderId).stream()
                .map(mapper::toPaymentResponse)
                .toList();
    }

    // ---------------------------------------------------------------------

    /** @return null si accepté, sinon le motif du refus. */
    private String simulateGateway(CheckoutRequest request) {
        if (Boolean.TRUE.equals(request.simulateFailure())) {
            return "refus simulé";
        }
        // Les cartes de test n'ont de sens que pour un paiement par carte (Stripe)
        String card = request.method() == PaymentMethod.STRIPE ? request.cardNumber() : null;
        if (card != null) {
            if (card.endsWith("0002")) return "carte déclinée par la banque émettrice";
            if (card.endsWith("9995")) return "fonds insuffisants";
        }
        return null;
    }

    private String generateTransactionRef(PaymentMethod method) {
        return switch (method) {
            case STRIPE -> "pi_" + random(24);
            case PAYPAL -> "PAYID-" + random(20).toUpperCase();
            case SIMULATOR -> "SIM-" + LocalDateTime.now().format(SIM_TS) + "-" + random(6).toUpperCase();
        };
    }

    private static String random(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }
}

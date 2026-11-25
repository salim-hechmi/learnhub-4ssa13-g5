package tn.esprit.learnhub.marketplace.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.learnhub.marketplace.entity.Invoice;
import tn.esprit.learnhub.marketplace.entity.Order;
import tn.esprit.learnhub.marketplace.entity.OrderItem;
import tn.esprit.learnhub.marketplace.entity.Payment;
import tn.esprit.learnhub.marketplace.enums.OrderStatus;
import tn.esprit.learnhub.marketplace.enums.PaymentMethod;
import tn.esprit.learnhub.marketplace.enums.PaymentStatus;
import tn.esprit.learnhub.marketplace.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Jeu de données de démonstration pour le backoffice (uniquement si la table orders est vide).
 * Désactivable avec marketplace.seed-demo-data=false.
 * Les données sont insérées directement (snapshot) : aucun appel Feign, aucun événement RabbitMQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "marketplace.seed-demo-data", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {

    // UUID Keycloak fictifs (les noms affichés sont dans le front : shared/student-directory.ts)
    public static final String YASMINE = "a1b2c3d4-0000-4000-8000-000000000001";
    public static final String KARIM = "a1b2c3d4-0000-4000-8000-000000000002";
    public static final String INES = "a1b2c3d4-0000-4000-8000-000000000003";
    public static final String MEHDI = "a1b2c3d4-0000-4000-8000-000000000004";
    public static final String SALMA = "a1b2c3d4-0000-4000-8000-000000000005";

    private record Course(long id, String title, String price) {
    }

    // Mêmes titres et prix que le catalogue simulé (StandaloneCourseClient)
    private static final Course WEB = new Course(1, "Développement web : HTML, CSS et JavaScript", "59.00");
    private static final Course PYTHON = new Course(2, "Python pour débutants", "45.00");
    private static final Course SQL = new Course(3, "Bases de données et SQL", "39.00");
    private static final Course REACT = new Course(4, "React : créer des interfaces modernes", "69.00");
    private static final Course EXCEL = new Course(5, "Excel pour la gestion de données", "35.00");
    private static final Course ANGLAIS = new Course(6, "Anglais professionnel (niveau B2)", "55.00");

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (orderRepository.count() > 0) {
            return;
        }
        LocalDateTime now = LocalDateTime.now().withNano(0);
        int seq = 1;
        seed(YASMINE, now.minusDays(13), OrderStatus.PAID, PaymentMethod.STRIPE, seq++, false, WEB, PYTHON);
        seed(KARIM, now.minusDays(12), OrderStatus.PAID, PaymentMethod.PAYPAL, seq++, false, PYTHON);
        seed(INES, now.minusDays(10), OrderStatus.PAID, PaymentMethod.STRIPE, seq++, true, SQL, REACT);
        seed(MEHDI, now.minusDays(9), OrderStatus.PAID, PaymentMethod.STRIPE, seq++, false, WEB);
        seed(SALMA, now.minusDays(8), OrderStatus.FAILED, PaymentMethod.STRIPE, seq++, false, ANGLAIS);
        seed(YASMINE, now.minusDays(7), OrderStatus.REFUNDED, PaymentMethod.PAYPAL, seq++, false, EXCEL);
        seed(KARIM, now.minusDays(5), OrderStatus.PAID, PaymentMethod.STRIPE, seq++, false, REACT, ANGLAIS);
        seed(INES, now.minusDays(4), OrderStatus.PENDING, null, seq++, false, WEB);
        seed(MEHDI, now.minusDays(3), OrderStatus.PAID, PaymentMethod.PAYPAL, seq++, false, REACT);
        seed(SALMA, now.minusDays(2), OrderStatus.PAID, PaymentMethod.STRIPE, seq++, true, WEB, EXCEL);
        seed(MEHDI, now.minusDays(1), OrderStatus.PENDING, null, seq++, false, ANGLAIS, SQL);
        seed(SALMA, now.minusHours(3), OrderStatus.FAILED, PaymentMethod.PAYPAL, seq, false, PYTHON);
        log.info("Données de démonstration Marketplace insérées : {} commandes", orderRepository.count());
    }

    private void seed(String student, LocalDateTime createdAt, OrderStatus status, PaymentMethod method,
                      int seq, boolean failedFirst, Course... courses) {
        Order order = Order.builder()
                .studentId(student)
                .status(status)
                .currency("TND")
                .createdAt(createdAt)
                .build();
        for (Course c : courses) {
            order.addItem(OrderItem.builder()
                    .courseId(c.id()).courseTitle(c.title()).unitPrice(new BigDecimal(c.price())).build());
        }
        order.recalculateTotal();

        if (failedFirst) {
            order.addPayment(payment(order, method, PaymentStatus.FAILED, seq, "F"));
        }
        if (status == OrderStatus.FAILED) {
            order.addPayment(payment(order, method, PaymentStatus.FAILED, seq, "X"));
        }
        if (status == OrderStatus.PAID || status == OrderStatus.REFUNDED) {
            LocalDateTime paidAt = createdAt.plusMinutes(4);
            order.setPaidAt(paidAt);
            order.addPayment(payment(order, method, PaymentStatus.SUCCESS, seq, "S"));
            Order saved = orderRepository.save(order);
            saved.attachInvoice(Invoice.builder()
                    .invoiceNumber(String.format("INV-%d-%06d", paidAt.getYear(), saved.getId()))
                    .issuedAt(paidAt)
                    .build());
            return;
        }
        orderRepository.save(order);
    }

    private Payment payment(Order order, PaymentMethod method, PaymentStatus status, int seq, String tag) {
        String ref = switch (method) {
            case STRIPE -> "pi_demo" + tag + "000000000000000" + String.format("%02d", seq);
            case PAYPAL -> "PAYID-DEMO" + tag + "0000000000" + String.format("%02d", seq);
            case SIMULATOR -> "SIM-DEMO-" + tag + String.format("%04d", seq);
        };
        return Payment.builder().amount(order.getTotalAmount()).method(method).status(status).transactionRef(ref).build();
    }

}

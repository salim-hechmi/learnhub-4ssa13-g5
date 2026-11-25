package tn.esprit.learnhub.marketplace.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tn.esprit.learnhub.marketplace.dto.event.OrderRefundedEvent;
import tn.esprit.learnhub.marketplace.dto.event.PaymentCompletedEvent;

/**
 * Pont "événement de domaine -> RabbitMQ".
 * <p>
 * Grâce à {@link TransactionalEventListener} (AFTER_COMMIT), PaymentCompleted n'est publié
 * QUE si la commande PAID et la facture sont réellement enregistrées en base :
 * jamais d'inscription déclenchée pour un paiement annulé par un rollback.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarketplaceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${marketplace.rabbitmq.exchange}")
    private String exchange;

    @Value("${marketplace.rabbitmq.payment-completed-routing-key}")
    private String paymentCompletedRoutingKey;

    @Value("${marketplace.rabbitmq.order-refunded-routing-key}")
    private String orderRefundedRoutingKey;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPaid(OrderPaidDomainEvent event) {
        // Un message PaymentCompleted par cours acheté (contrat { studentId, courseId, orderId, paidAt })
        for (Long courseId : event.courseIds()) {
            PaymentCompletedEvent message =
                    new PaymentCompletedEvent(event.studentId(), courseId, event.orderId(), event.paidAt());
            send(paymentCompletedRoutingKey, message);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderRefunded(OrderRefundedEvent event) {
        send(orderRefundedRoutingKey, event);
    }

    private void send(String routingKey, Object payload) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, payload);
            log.info("[RabbitMQ] {} -> {} : {}", exchange, routingKey, payload);
        } catch (AmqpException ex) {
            // Le paiement est déjà validé en base : on journalise sans casser la réponse HTTP.
            log.error("[RabbitMQ] Publication impossible ({}): {} — payload={}", routingKey, ex.getMessage(), payload);
        }
    }
}

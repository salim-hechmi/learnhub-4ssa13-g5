package tn.esprit.learnhub.marketplace.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Topologie RabbitMQ du Marketplace :
 *
 *   learnhub.marketplace.exchange (Topic, durable)
 *        └── payment.completed ──► journey.payment-completed.queue  (consommée par learning-journey-service)
 *                                   └── DLQ : journey.payment-completed.queue.dlq
 *        └── order.refunded    ──► (libre aux autres services de s'y abonner)
 */
@Configuration
public class RabbitMQConfig {

    @Value("${marketplace.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${marketplace.rabbitmq.payment-completed-queue}")
    private String paymentCompletedQueue;

    @Value("${marketplace.rabbitmq.payment-completed-routing-key}")
    private String paymentCompletedRoutingKey;

    @Bean
    public TopicExchange marketplaceExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    /** Dead-letter exchange : les messages en échec côté consommateur ne sont pas perdus. */
    @Bean
    public TopicExchange marketplaceDeadLetterExchange() {
        return new TopicExchange(exchangeName + ".dlx", true, false);
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return QueueBuilder.durable(paymentCompletedQueue)
                .withArgument("x-dead-letter-exchange", exchangeName + ".dlx")
                .withArgument("x-dead-letter-routing-key", paymentCompletedRoutingKey + ".dlq")
                .build();
    }

    @Bean
    public Queue paymentCompletedDeadLetterQueue() {
        return QueueBuilder.durable(paymentCompletedQueue + ".dlq").build();
    }

    @Bean
    public Binding paymentCompletedBinding() {
        return BindingBuilder.bind(paymentCompletedQueue())
                .to(marketplaceExchange())
                .with(paymentCompletedRoutingKey);
    }

    @Bean
    public Binding paymentCompletedDeadLetterBinding() {
        return BindingBuilder.bind(paymentCompletedDeadLetterQueue())
                .to(marketplaceDeadLetterExchange())
                .with(paymentCompletedRoutingKey + ".dlq");
    }

    /** Sérialisation JSON des événements (LocalDateTime au format ISO-8601). */
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        template.setExchange(exchangeName);
        return template;
    }
}

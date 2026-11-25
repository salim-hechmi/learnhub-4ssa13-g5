package com.learnhub.course.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ du course-service.
 * <p>
 * Déclare :
 * - Topic Exchange : learnhub.topic
 * - Queue : course.published.queue
 * - Binding : course.published -> learnhub.topic avec routing-key "course.published"
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.topic}")
    private String topicExchange;

    @Value("${rabbitmq.queue.course-published}")
    private String coursePublishedQueue;

    @Value("${rabbitmq.routing-key.course-published}")
    private String coursePublishedRoutingKey;

    // ---- Exchange ----
    @Bean
    public TopicExchange learnhubTopicExchange() {
        return new TopicExchange(topicExchange, true, false);
    }

    // ---- Queue ----
    @Bean
    public Queue coursePublishedQueue() {
        return QueueBuilder.durable(coursePublishedQueue).build();
    }

    // ---- Binding ----
    @Bean
    public Binding coursePublishedBinding(Queue coursePublishedQueue, TopicExchange learnhubTopicExchange) {
        return BindingBuilder
                .bind(coursePublishedQueue)
                .to(learnhubTopicExchange)
                .with(coursePublishedRoutingKey);
    }

    // ---- Convertisseur JSON ----
    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter());
        return template;
    }
}

package com.learnhub.course.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Publie les événements RabbitMQ du course-service.
 * <p>
 * Événement publié : "CoursePublished"
 * Exchange : learnhub.topic
 * Routing key : course.published
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CourseEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.topic}")
    private String topicExchange;

    @Value("${rabbitmq.routing-key.course-published}")
    private String coursePublishedKey;

    /**
     * Publie l'événement "CoursePublished" sur RabbitMQ.
     *
     * @param courseId     ID du cours publié
     * @param courseTitle  Titre du cours
     * @param instructorId UUID Keycloak de l'instructeur
     */
    public void publishCoursePublished(Integer courseId, String courseTitle, String instructorId) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "CoursePublished");
        event.put("courseId", courseId);
        event.put("courseTitle", courseTitle);
        event.put("instructorId", instructorId);
        event.put("publishedAt", LocalDateTime.now().toString());

        try {
            rabbitTemplate.convertAndSend(topicExchange, coursePublishedKey, event);
            log.info("Événement RabbitMQ publié : CoursePublished -> courseId={}", courseId);
        } catch (Exception e) {
            log.error("Erreur lors de la publication de l'événement RabbitMQ CoursePublished : {}", e.getMessage());
            // On ne bloque pas l'opération si RabbitMQ est indisponible
        }
    }
}

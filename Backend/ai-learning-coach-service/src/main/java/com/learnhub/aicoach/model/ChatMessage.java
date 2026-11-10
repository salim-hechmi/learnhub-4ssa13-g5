package com.learnhub.aicoach.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier ChatMessage propre au contexte borné de ai-learning-coach-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "chatmessages")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sessionId;
    private String senderRole;
    private String messageText;
    private java.time.LocalDateTime sentAt = java.time.LocalDateTime.now();

    public ChatMessage() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

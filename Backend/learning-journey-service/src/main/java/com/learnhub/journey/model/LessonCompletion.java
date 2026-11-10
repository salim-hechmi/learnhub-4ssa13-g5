package com.learnhub.journey.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier LessonCompletion propre au contexte borné de learning-journey-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "lessoncompletions")
public class LessonCompletion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long progressId;
    private Long resourceId;
    private Boolean completed = true;
    private java.time.LocalDateTime completedAt = java.time.LocalDateTime.now();

    public LessonCompletion() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

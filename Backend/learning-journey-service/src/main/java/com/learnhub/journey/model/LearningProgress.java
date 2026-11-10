package com.learnhub.journey.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier LearningProgress propre au contexte borné de learning-journey-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "learningprogresss")
public class LearningProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long enrollmentId;
    private Double completionPercentage = 0.0;
    private Integer completedLessonsCount = 0;
    private java.time.LocalDateTime lastAccessedAt = java.time.LocalDateTime.now();

    public LearningProgress() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

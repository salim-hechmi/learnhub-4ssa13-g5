package com.learnhub.assessment.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier StudentAttempt propre au contexte borné de assessment-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "studentattempts")
public class StudentAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long quizId;
    private Long studentId;
    private Double score;
    private Boolean passed;
    private java.time.LocalDateTime attemptedAt = java.time.LocalDateTime.now();

    public StudentAttempt() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

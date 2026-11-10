package com.learnhub.assessment.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier Quiz propre au contexte borné de assessment-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "quizs")
public class Quiz implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long courseId;
    private String title;
    private Double passingScore = 70.0;
    private Integer maxAttempts = 3;

    public Quiz() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

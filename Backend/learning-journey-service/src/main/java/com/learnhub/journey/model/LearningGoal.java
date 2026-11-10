package com.learnhub.journey.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier LearningGoal propre au contexte borné de learning-journey-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "learninggoals")
public class LearningGoal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private String targetSkill;
    private Integer targetHoursPerWeek;
    private java.time.LocalDate targetDate;

    public LearningGoal() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

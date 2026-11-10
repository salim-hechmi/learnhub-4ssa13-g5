package com.learnhub.assessment.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier Certificate propre au contexte borné de assessment-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "certificates")
public class Certificate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentAttemptId;
    private Long studentId;
    private Long courseId;
    private String certificateCode;
    private java.time.LocalDateTime issueDate = java.time.LocalDateTime.now();
    private String verificationUrl;

    public Certificate() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

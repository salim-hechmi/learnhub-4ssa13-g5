package com.learnhub.community.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier Badge propre au contexte borné de community-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "badges")
public class Badge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private String badgeType;
    private String title;
    private String iconUrl;
    private java.time.LocalDateTime earnedAt = java.time.LocalDateTime.now();

    public Badge() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

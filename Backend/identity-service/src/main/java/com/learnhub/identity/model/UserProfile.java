package com.learnhub.identity.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier UserProfile propre au contexte borné de identity-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "userprofiles")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String bio;
    private String avatarUrl;
    private String headline;
    private String linkedInUrl;
    private String githubUrl;

    public UserProfile() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

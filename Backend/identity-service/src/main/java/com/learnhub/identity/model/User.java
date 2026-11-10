package com.learnhub.identity.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier User propre au contexte borné de identity-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean enabled = true;
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    public User() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

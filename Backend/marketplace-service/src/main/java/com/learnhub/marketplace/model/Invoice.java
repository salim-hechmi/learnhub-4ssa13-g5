package com.learnhub.marketplace.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier Invoice propre au contexte borné de marketplace-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "invoices")
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String invoiceNumber;
    private Double totalAmount;
    private java.time.LocalDateTime issuedAt = java.time.LocalDateTime.now();
    private String downloadUrl;

    public Invoice() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

package com.learnhub.community.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

/** Catalogue des badges (table "badges"). */
@Data
@Entity
@Table(name = "badges", uniqueConstraints = @UniqueConstraint(name = "uniq_badge_code", columnNames = "code"))
public class Badge {
    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;
    @NotBlank @Column(nullable = false) private String code; // ex: AWD_ARCHITECT
    @NotBlank @Column(nullable = false) private String name;
    private String description;
    @Column(length = 50) private String icon;
    private String triggerEvent; // ex: CertificateIssued (RabbitMQ)
}

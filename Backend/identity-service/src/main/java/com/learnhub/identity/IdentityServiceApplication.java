package com.learnhub.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du microservice identity-service
 * Service commun Identity & User Management - Authentification JWT, rôles et profils
 * Port configuré : 8081
 */
@SpringBootApplication
public class IdentityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("  LearnHub - identity-service démarré avec succès !");
        System.out.println("  Swagger UI : http://localhost:8081/swagger-ui.html");
        System.out.println("  API Docs   : http://localhost:8081/v3/api-docs");
        System.out.println("  H2 Console : http://localhost:8081/h2-console");
        System.out.println("==================================================================");
    }
}

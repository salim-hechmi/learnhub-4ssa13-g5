package com.learnhub.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du microservice marketplace-service
 * Microservice Marketplace & Billing (Responsable : Selim Hechmi) - Commandes, paiements simulés et factures
 * Port configuré : 8085
 */
@SpringBootApplication
public class MarketplaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceServiceApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("  LearnHub - marketplace-service démarré avec succès !");
        System.out.println("  Swagger UI : http://localhost:8085/swagger-ui.html");
        System.out.println("  API Docs   : http://localhost:8085/v3/api-docs");
        System.out.println("  H2 Console : http://localhost:8085/h2-console");
        System.out.println("==================================================================");
    }
}

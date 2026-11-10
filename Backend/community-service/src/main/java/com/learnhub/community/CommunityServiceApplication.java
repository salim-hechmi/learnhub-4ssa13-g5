package com.learnhub.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du microservice community-service
 * Microservice Community & Feedback - Avis cours, fils de discussion, réponses et badges d engagement
 * Port configuré : 8086
 */
@SpringBootApplication
public class CommunityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityServiceApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("  LearnHub - community-service démarré avec succès !");
        System.out.println("  Swagger UI : http://localhost:8086/swagger-ui.html");
        System.out.println("  API Docs   : http://localhost:8086/v3/api-docs");
        System.out.println("  H2 Console : http://localhost:8086/h2-console");
        System.out.println("==================================================================");
    }
}

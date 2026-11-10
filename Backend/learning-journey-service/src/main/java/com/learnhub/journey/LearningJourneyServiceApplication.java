package com.learnhub.journey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du microservice learning-journey-service
 * Microservice Learning Journey - Inscriptions, suivi de la progression et complétion des leçons
 * Port configuré : 8083
 */
@SpringBootApplication
public class LearningJourneyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningJourneyServiceApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("  LearnHub - learning-journey-service démarré avec succès !");
        System.out.println("  Swagger UI : http://localhost:8083/swagger-ui.html");
        System.out.println("  API Docs   : http://localhost:8083/v3/api-docs");
        System.out.println("  H2 Console : http://localhost:8083/h2-console");
        System.out.println("==================================================================");
    }
}

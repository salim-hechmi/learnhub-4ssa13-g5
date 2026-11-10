package com.learnhub.aicoach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du microservice ai-learning-coach-service
 * Microservice AI Learning Coach (Service Avancé) - Plans d étude personnalisés, recommandations intelligentes et chatbot
 * Port configuré : 8087
 */
@SpringBootApplication
public class AiLearningCoachServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLearningCoachServiceApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("  LearnHub - ai-learning-coach-service démarré avec succès !");
        System.out.println("  Swagger UI : http://localhost:8087/swagger-ui.html");
        System.out.println("  API Docs   : http://localhost:8087/v3/api-docs");
        System.out.println("  H2 Console : http://localhost:8087/h2-console");
        System.out.println("==================================================================");
    }
}

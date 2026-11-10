package com.learnhub.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du microservice assessment-service
 * Microservice Assessment & Certification - Quiz, questions, tentatives, scores et certificats
 * Port configuré : 8084
 */
@SpringBootApplication
public class AssessmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssessmentServiceApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("  LearnHub - assessment-service démarré avec succès !");
        System.out.println("  Swagger UI : http://localhost:8084/swagger-ui.html");
        System.out.println("  API Docs   : http://localhost:8084/v3/api-docs");
        System.out.println("  H2 Console : http://localhost:8084/h2-console");
        System.out.println("==================================================================");
    }
}

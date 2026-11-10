package com.learnhub.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du microservice course-service
 * Microservice Course Management - Catalogue, cours, chapitres et ressources pédagogiques
 * Port configuré : 8082
 */
@SpringBootApplication
public class CourseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("  LearnHub - course-service démarré avec succès !");
        System.out.println("  Swagger UI : http://localhost:8082/swagger-ui.html");
        System.out.println("  API Docs   : http://localhost:8082/v3/api-docs");
        System.out.println("  H2 Console : http://localhost:8082/h2-console");
        System.out.println("==================================================================");
    }
}

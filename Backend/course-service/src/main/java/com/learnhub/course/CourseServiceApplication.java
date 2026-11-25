package com.learnhub.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Point d'entrée principal du microservice course-service.
 *
 * <p>Responsable : Nawres Soua - 4SSA13 Groupe G5 - LearnHub</p>
 *
 * <p>Ce microservice gère :
 * <ul>
 *   <li>Le catalogue des cours (CRUD complet)</li>
 *   <li>Les catégories de cours</li>
 *   <li>Les chapitres et les leçons (curriculum)</li>
 * </ul>
 * </p>
 *
 * <p>Enregistré comme client Eureka sur le port 8082.</p>
 */
@SpringBootApplication
@EnableDiscoveryClient   // Enregistrement Eureka
@EnableFeignClients      // Communication synchrone OpenFeign
public class CourseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }
}

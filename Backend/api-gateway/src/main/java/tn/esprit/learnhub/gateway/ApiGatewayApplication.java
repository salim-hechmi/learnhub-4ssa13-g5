package tn.esprit.learnhub.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway LearnHub : point d'accès unique (port 8080).
 * Toutes les requêtes du frontend et de Postman passent par ici, puis sont routées
 * vers les microservices trouvés dans Eureka (URI lb://nom-du-service).
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

package tn.esprit.learnhub.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * LearnHub - Microservice 5 : marketplace-service (port 8085).
 * Commandes d'achat, paiement sandbox simulé (Stripe / PayPal / Simulateur), factures.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MarketplaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceServiceApplication.class, args);
    }
}

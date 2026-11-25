package tn.esprit.learnhub.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Annuaire de services LearnHub.
 * Lancé deux fois (profils peer1 et peer2) : chaque instance s'enregistre auprès de l'autre
 * et lui réplique son registre, donc les deux dashboards affichent tous les microservices.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

package tn.esprit.learnhub.gateway.loadbalancer;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;

/**
 * Algorithme de répartition de charge choisi PAR microservice.
 * Les autres services gardent l'algorithme par défaut de Spring Cloud LoadBalancer (Round Robin).
 *
 *  - course-service      -> Random   : chaque requête part sur une instance tirée au hasard (8082 ou 8092)
 *  - marketplace-service -> Weighted : répartition selon le poids déclaré par chaque instance dans Eureka
 *                                      (metadata "weight" : 8085 = 3, 8095 = 1, soit 75 % / 25 %)
 */
@Configuration
@LoadBalancerClients({
        @LoadBalancerClient(name = "course-service", configuration = RandomLoadBalancerConfig.class),
        @LoadBalancerClient(name = "marketplace-service", configuration = WeightedLoadBalancerConfig.class)
})
public class LoadBalancingConfig {
}

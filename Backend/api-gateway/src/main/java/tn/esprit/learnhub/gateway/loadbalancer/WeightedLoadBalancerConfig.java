package tn.esprit.learnhub.gateway.loadbalancer;

import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Algorithme Weighted (pondéré), appliqué uniquement à marketplace-service.
 * Chaque instance annonce son poids dans Eureka (eureka.instance.metadata-map.weight).
 * La liste des instances est répétée selon ces poids : avec 8085 = 3 et 8095 = 1,
 * sur 4 requêtes, 3 vont sur 8085 et 1 sur 8095. Une instance sans poids compte pour 1.
 * Sans @Configuration pour la même raison que RandomLoadBalancerConfig.
 */
public class WeightedLoadBalancerConfig {

    @Bean
    public ServiceInstanceListSupplier weightedServiceInstanceListSupplier(ConfigurableApplicationContext context) {
        return ServiceInstanceListSupplier.builder()
                .withDiscoveryClient()
                .withWeighted()
                .build(context);
    }
}

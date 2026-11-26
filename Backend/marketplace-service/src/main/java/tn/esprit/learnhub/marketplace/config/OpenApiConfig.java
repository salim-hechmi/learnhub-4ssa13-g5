package tn.esprit.learnhub.marketplace.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    static {
        // CurrentUser est résolu depuis les en-têtes : on le masque des paramètres de requête Swagger
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(CurrentUser.class);
    }

    @Bean
    public OpenAPI marketplaceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LearnHub — Marketplace & Billing API")
                        .version("1.0.0")
                        .description("""
                                Microservice 5 (port 8085) : commandes, paiement sandbox simulé (Stripe / PayPal / Simulateur), factures.
                                Identité propagée par l'API Gateway via les en-têtes X-User-Id, X-User-Roles, X-User-Email.
                                Publie l'événement PaymentCompleted sur RabbitMQ (exchange learnhub.marketplace.exchange).""")
                        .contact(new Contact().name("Selim Hechmi — Team Lead G5 (4SSA13)")));
        // Pas de serveur écrit en dur : springdoc le déduit de la requête.
        // Ouvert via la Gateway (Swagger unifié, port 8080) -> "Try it out" passe par la Gateway ;
        // ouvert en direct (8085 ou 8095) -> appelle cette instance.
    }

    /** Ajoute les en-têtes d'identité de la Gateway à chaque opération qui reçoit un CurrentUser. */
    @Bean
    public OperationCustomizer gatewayHeadersCustomizer() {
        return (operation, handlerMethod) -> {
            boolean needsUser = Arrays.stream(handlerMethod.getMethodParameters())
                    .anyMatch(p -> CurrentUser.class.equals(p.getParameterType()));
            if (needsUser) {
                operation.addParametersItem(new HeaderParameter().name(CurrentUser.HEADER_ID).required(true)
                        .description("UUID Keycloak de l'utilisateur (posé par la Gateway)")
                        .schema(new StringSchema()._default("a1b2c3d4-0000-4000-8000-000000000001")));
                operation.addParametersItem(new HeaderParameter().name(CurrentUser.HEADER_ROLES).required(false)
                        .description("Rôles séparés par des virgules : STUDENT, INSTRUCTOR, ADMIN")
                        .schema(new StringSchema()._default("STUDENT")));
                operation.addParametersItem(new HeaderParameter().name(CurrentUser.HEADER_EMAIL).required(false)
                        .schema(new StringSchema()));
            }
            return operation;
        };
    }
}

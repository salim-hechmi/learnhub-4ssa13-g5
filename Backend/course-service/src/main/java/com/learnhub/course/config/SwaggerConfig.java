package com.learnhub.course.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Swagger / OpenAPI 3 avec springdoc 2.6.0.
 * UI disponible sur : http://localhost:8082/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI courseServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LearnHub - Course Service API")
                        .description("Microservice de gestion du catalogue de cours, chapitres et leçons.\n\n" +
                                     "**Responsable :** Nawres Soua\n" +
                                     "**Classe :** 4SSA13 - Groupe G5 - ESPRIT\n" +
                                     "**Module :** AWD - Application Web Distribuée")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nawres Soua")
                                .email("nawres.soua@esprit.tn"))
                        .license(new License()
                                .name("ESPRIT University - Academic Project")
                                .url("https://github.com/salim-hechmi/learnhub-4ssa13-g5")));
    }
}

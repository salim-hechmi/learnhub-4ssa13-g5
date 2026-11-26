# api-gateway (LearnHub, port 8080)

Point d'accès **unique** de LearnHub : le frontend et Postman appellent uniquement `http://localhost:8080`.
La Gateway trouve les microservices dans Eureka (`lb://nom-du-service`) et leur transmet la requête.

| Fonction | Où |
|---|---|
| Routes vers les 7 microservices | `src/main/resources/application.yml` (`spring.cloud.gateway.routes`) |
| Swagger unifié | `springdoc.swagger-ui.urls` + routes `docs-*` ; page : http://localhost:8080/swagger-ui.html |
| Load balancing Random (course-service) et Weighted (marketplace-service) | `loadbalancer/LoadBalancingConfig.java` |
| Circuit breaker + fallback (route marketplace-service) | route `marketplace-service` (filtre `CircuitBreaker`), `fallback/FallbackController.java` |
| Routes, état des circuits | http://localhost:8080/actuator/gateway/routes · http://localhost:8080/actuator/circuitbreakers |

## Lancer

```powershell
cd Backend
powershell -ExecutionPolicy Bypass -File .\start-all.ps1      # Eureka x2, course x2, marketplace x2, puis la Gateway
```

Seule : `mvn spring-boot:run` dans `Backend/api-gateway` (Eureka doit tourner).

## Routes

| Microservice (responsable) | Chemins routés | Algorithme |
|---|---|---|
| marketplace-service (Selim) | `/api/orders/**`, `/api/payments/**`, `/api/invoices/**` | Weighted (8085 = 3, 8095 = 1) + circuit breaker |
| course-service (Nawres) | `/api/courses/**`, `/api/categories/**`, `/api/chapters/**`, `/api/lessons/**` | Random |
| community-service (Mootez) | `/api/courses/*/reviews/**`, `/api/courses/*/threads/**`, `/api/threads/**`, `/api/students/*/badges` | Round Robin |
| learning-journey-service (Aziz) | `/api/enrollments/**`, `/api/learning-goals/**`, `/api/students/*/enrollments`, `/api/lessons/*/completion` | Round Robin |
| assessment-service (Adam) | `/api/quizzes/**`, `/api/attempts/**`, `/api/certificates/**` | Round Robin |
| ai-learning-coach-service (Adam) | `/api/chat/**`, `/api/study-plans/**`, `/api/students/*/recommendations` | Round Robin |
| identity-service (équipe) | `/api/login`, `/api/register`, `/api/me`, `/api/*/roles` | Round Robin |

Les routes plus précises ont un `order` plus petit : `/api/courses/1/reviews` va à community-service, `/api/courses/1` à course-service.

## Pour qu'un microservice soit joignable via la Gateway

1. Il est **client Eureka** (`spring-cloud-starter-netflix-eureka-client` + bloc `eureka:` dans son yml) avec `spring.application.name` égal au nom utilisé dans la route (ex. `community-service`).
2. Il est démarré. Sinon la Gateway répond 503 (aucune instance trouvée dans Eureka).
3. Sa doc OpenAPI est sur `/v3/api-docs` (course-service : `/api-docs`) pour apparaître dans le Swagger unifié.

## Circuit breaker (marketplaceCB)

Fenêtre de 10 appels, minimum 5, seuil 50 % d'échecs, 15 s en état OPEN, délai max 5 s par appel.
Une réponse 500, 502, 503 ou 504 du service compte comme un échec.
Test : arrêter les ports 8085 et 8095, appeler `GET http://localhost:8080/api/orders/stats` plusieurs fois (dossier 4 de la collection Postman).

Collection Postman : `postman/LearnHub-Gateway.postman_collection.json`.

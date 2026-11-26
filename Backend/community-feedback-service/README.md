# LearnHub — Community & Feedback (port 8086, MySQL)

## Lancer
```bash
docker compose up -d --build
```
- API REST ........ http://localhost:8086/api/reviews
- Index API ....... http://localhost:8086/api
- phpMyAdmin ...... http://localhost:8089  (visualiser la BD)
- Backoffice ...... http://localhost:8086/
- Postman ......... importer `postman/LearnHub_Community_Feedback.postman_collection.json` puis Run collection

## Découverte Eureka
Le service s'enregistre sous le nom `community-feedback-service` auprès de
`http://localhost:8761/eureka/` par défaut. Définir `EUREKA_URL` si le serveur Eureka
utilise une autre adresse. Avec Docker, cette URL doit être joignable depuis le conteneur
(par exemple `http://host.docker.internal:8761/eureka/` si Eureka tourne sur l'hôte).

## Sans Docker (dev)
JDK 25 LTS et Maven sont requis.

1. Lancer MySQL 8 en local (port 3306), puis `mysql -uroot -p < mysql-init/init.sql`
   (identifiants modifiables via les variables `DB_URL`, `DB_USER`, `DB_PASSWORD`)
2. `mvn spring-boot:run`

## Endpoints
| Ressource | Endpoints |
|---|---|
| Avis | GET/POST `/api/reviews`, GET/PUT/DELETE `/api/reviews/{id}`, GET `/api/reviews/search?q=&status=`, GET `/api/reviews/course/{courseId}`, PATCH `/api/reviews/{id}/moderate?status=` |
| Fils | GET/POST `/api/threads`, GET/PUT/DELETE `/api/threads/{id}`, POST `/api/threads/{id}/replies`, GET `/api/threads/course/{courseId}` |
| Badges | GET/POST `/api/badges`, GET/PUT/DELETE `/api/badges/{id}` |
| Association | GET/POST `/api/user-badges`, GET/PUT/DELETE `/api/user-badges/{id}`, GET `/api/user-badges/user/{userId}` |

Codes : 201 création, 204 suppression, 400 validation, 404 introuvable, 409 doublon (contrainte unique).

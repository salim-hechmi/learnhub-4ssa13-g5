# course-service

Microservice Course Management - Catalogue, cours, chapitres et ressources pédagogiques

## 📌 Informations techniques
- **Port d'écoute :** `8082`
- **Base de données dédiée :** `learnhub_course_db` (H2 en mémoire par défaut, compatible MySQL)
- **Framework :** Spring Boot 3.4.3 / Java 17+
- **Documentation OpenAPI / Swagger UI :** [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **Spécification OpenAPI JSON :** [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs)

## 🧩 Entités métier du Bounded Context
- **`Category`** : Gestion autonome des données du domaine
- **`Course`** : Gestion autonome des données du domaine
- **`Chapter`** : Gestion autonome des données du domaine
- **`LearningResource`** : Gestion autonome des données du domaine

## 🚀 Lancement rapide
```bash
# Compilation et exécution
mvn clean spring-boot:run
```

## 📡 Endpoints exposés
| Méthode | URI | Code statut | Description |
|---|---|---|---|
| `GET` | `/api` | `200` | Lister les cours publiés du catalogue |
| `GET` | `/api/{id}` | `200` | Consulter les détails d un cours par son ID |
| `POST` | `/api` | `201` | Créer un nouveau cours (Formateur) |
| `PUT` | `/api/{id}` | `200` | Modifier les informations d un cours |
| `POST` | `/api/{id}/publish` | `200` | Publier officiellement un cours dans le catalogue |
| `POST` | `/api/{id}/chapters` | `201` | Ajouter un chapitre à un cours existant |

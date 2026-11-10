# community-service

Microservice Community & Feedback - Avis cours, fils de discussion, réponses et badges d engagement

## 📌 Informations techniques
- **Port d'écoute :** `8086`
- **Base de données dédiée :** `learnhub_community_db` (H2 en mémoire par défaut, compatible MySQL)
- **Framework :** Spring Boot 3.4.3 / Java 17+
- **Documentation OpenAPI / Swagger UI :** [http://localhost:8086/swagger-ui.html](http://localhost:8086/swagger-ui.html)
- **Spécification OpenAPI JSON :** [http://localhost:8086/v3/api-docs](http://localhost:8086/v3/api-docs)

## 🧩 Entités métier du Bounded Context
- **`CourseReview`** : Gestion autonome des données du domaine
- **`DiscussionThread`** : Gestion autonome des données du domaine
- **`Post`** : Gestion autonome des données du domaine
- **`Badge`** : Gestion autonome des données du domaine

## 🚀 Lancement rapide
```bash
# Compilation et exécution
mvn clean spring-boot:run
```

## 📡 Endpoints exposés
| Méthode | URI | Code statut | Description |
|---|---|---|---|
| `POST` | `/api/courses/{courseId}/reviews` | `201` | Publier un avis vérifié sur un cours (409 si déjà publié) |
| `GET` | `/api/courses/{courseId}/reviews` | `200` | Consulter les avis et commentaires d un cours |
| `POST` | `/api/courses/{courseId}/threads` | `201` | Créer un nouveau fil de discussion pour un cours |
| `GET` | `/api/courses/{courseId}/threads` | `200` | Lister les fils de discussion d un cours |
| `POST` | `/api/threads/{id}/posts` | `201` | Répondre à un fil de discussion |
| `PUT` | `/api/threads/{id}/close` | `200` | Fermer un fil de discussion (Formateur/Admin) |
| `GET` | `/api/students/{id}/badges` | `200` | Consulter les badges d engagement d un étudiant |

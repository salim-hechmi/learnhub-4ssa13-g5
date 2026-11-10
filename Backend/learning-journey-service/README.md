# learning-journey-service

Microservice Learning Journey - Inscriptions, suivi de la progression et complétion des leçons

## 📌 Informations techniques
- **Port d'écoute :** `8083`
- **Base de données dédiée :** `learnhub_journey_db` (H2 en mémoire par défaut, compatible MySQL)
- **Framework :** Spring Boot 3.4.3 / Java 17+
- **Documentation OpenAPI / Swagger UI :** [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
- **Spécification OpenAPI JSON :** [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs)

## 🧩 Entités métier du Bounded Context
- **`Enrollment`** : Gestion autonome des données du domaine
- **`LearningProgress`** : Gestion autonome des données du domaine
- **`LessonCompletion`** : Gestion autonome des données du domaine
- **`LearningGoal`** : Gestion autonome des données du domaine

## 🚀 Lancement rapide
```bash
# Compilation et exécution
mvn clean spring-boot:run
```

## 📡 Endpoints exposés
| Méthode | URI | Code statut | Description |
|---|---|---|---|
| `POST` | `/api/enrollments` | `201` | Créer une nouvelle inscription à un cours |
| `GET` | `/api/students/{id}/enrollments` | `200` | Consulter toutes les inscriptions d un étudiant |
| `GET` | `/api/enrollments/{id}/progress` | `200` | Consulter la progression détaillée d une inscription |
| `PUT` | `/api/lessons/{id}/completion` | `200` | Marquer une ressource/leçon comme terminée |
| `POST` | `/api/learning-goals` | `201` | Définir un nouvel objectif d apprentissage |

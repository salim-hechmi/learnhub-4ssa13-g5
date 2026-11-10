# assessment-service

Microservice Assessment & Certification - Quiz, questions, tentatives, scores et certificats

## 📌 Informations techniques
- **Port d'écoute :** `8084`
- **Base de données dédiée :** `learnhub_assessment_db` (H2 en mémoire par défaut, compatible MySQL)
- **Framework :** Spring Boot 3.4.3 / Java 17+
- **Documentation OpenAPI / Swagger UI :** [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)
- **Spécification OpenAPI JSON :** [http://localhost:8084/v3/api-docs](http://localhost:8084/v3/api-docs)

## 🧩 Entités métier du Bounded Context
- **`Quiz`** : Gestion autonome des données du domaine
- **`Question`** : Gestion autonome des données du domaine
- **`StudentAttempt`** : Gestion autonome des données du domaine
- **`Certificate`** : Gestion autonome des données du domaine

## 🚀 Lancement rapide
```bash
# Compilation et exécution
mvn clean spring-boot:run
```

## 📡 Endpoints exposés
| Méthode | URI | Code statut | Description |
|---|---|---|---|
| `POST` | `/api/quizzes` | `201` | Créer un nouveau quiz d évaluation pour un cours |
| `GET` | `/api/quizzes/{id}` | `200` | Obtenir les questions d un quiz |
| `POST` | `/api/quizzes/{id}/attempts` | `201` | Soumettre les réponses d un étudiant et calculer le score |
| `GET` | `/api/attempts/{id}/result` | `200` | Consulter le résultat détaillé d une tentative |
| `GET` | `/api/certificates/{id}` | `200` | Consulter et vérifier un certificat d accomplissement |

# ai-learning-coach-service

Microservice AI Learning Coach (Service Avancé) - Plans d étude personnalisés, recommandations intelligentes et chatbot

## 📌 Informations techniques
- **Port d'écoute :** `8087`
- **Base de données dédiée :** `learnhub_aicoach_db` (H2 en mémoire par défaut, compatible MySQL)
- **Framework :** Spring Boot 3.4.3 / Java 17+
- **Documentation OpenAPI / Swagger UI :** [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html)
- **Spécification OpenAPI JSON :** [http://localhost:8087/v3/api-docs](http://localhost:8087/v3/api-docs)

## 🧩 Entités métier du Bounded Context
- **`StudyPlan`** : Gestion autonome des données du domaine
- **`AIRecommendation`** : Gestion autonome des données du domaine
- **`ChatSession`** : Gestion autonome des données du domaine
- **`ChatMessage`** : Gestion autonome des données du domaine

## 🚀 Lancement rapide
```bash
# Compilation et exécution
mvn clean spring-boot:run
```

## 📡 Endpoints exposés
| Méthode | URI | Code statut | Description |
|---|---|---|---|
| `POST` | `/api/study-plans` | `201` | Générer un plan d étude personnalisé selon l objectif et la disponibilité |
| `GET` | `/api/students/{id}/recommendations` | `200` | Obtenir les recommandations de cours personnalisées par l IA |
| `POST` | `/api/chat/sessions` | `201` | Démarrer une session de conversation avec le coach IA |
| `POST` | `/api/chat/sessions/{id}/messages` | `201` | Envoyer une question au coach IA et recevoir une réponse adaptée |

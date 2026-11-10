# identity-service

Service commun Identity & User Management - Authentification JWT, rôles et profils

## 📌 Informations techniques
- **Port d'écoute :** `8081`
- **Base de données dédiée :** `learnhub_identity_db` (H2 en mémoire par défaut, compatible MySQL)
- **Framework :** Spring Boot 3.4.3 / Java 17+
- **Documentation OpenAPI / Swagger UI :** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Spécification OpenAPI JSON :** [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

## 🧩 Entités métier du Bounded Context
- **`User`** : Gestion autonome des données du domaine
- **`UserProfile`** : Gestion autonome des données du domaine
- **`Role`** : Gestion autonome des données du domaine
- **`Permission`** : Gestion autonome des données du domaine

## 🚀 Lancement rapide
```bash
# Compilation et exécution
mvn clean spring-boot:run
```

## 📡 Endpoints exposés
| Méthode | URI | Code statut | Description |
|---|---|---|---|
| `POST` | `/api/register` | `201` | Inscription d un nouvel utilisateur |
| `POST` | `/api/login` | `200` | Connexion utilisateur et émission du jeton JWT |
| `GET` | `/api/me` | `200` | Récupérer le profil de l utilisateur connecté |
| `PUT` | `/api/me` | `200` | Mettre à jour le profil utilisateur |
| `GET` | `/api/{id}/roles` | `200` | Consulter les rôles d un utilisateur |

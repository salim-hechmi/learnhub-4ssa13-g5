# Backend LearnHub - Architecture Microservices Spring Boot

Ce dossier regroupe l'ensemble des **7 microservices** composant l'architecture applicative de la plateforme **LearnHub** (Module AWD - Année 2026/2027) :
- **6 microservices métier** correspondant chacun à une capacité métier (Bounded Context) étanche.
- **1 microservice commun** d'authentification, de rôles et de gestion des utilisateurs.

Chaque microservice dispose de sa **propre base de données dédiée** (aucune table partagée), de son propre cycle de vie et de sa configuration d'écoute réseau.

---

## 📋 Matrice des Microservices

| Service | Port | Base de données | Responsabilité Métier | Responsable de l'équipe |
|---|:---:|---|---|---|
| **`identity-service`** | `8081` | MySQL (`learnhub_identity_db`) | Inscription, connexion JWT, profils et gestion des rôles (Admin/Formateur/Étudiant) | *Projet commun (Équipe entière)* |
| **`course-service`** | `8082` | MySQL (`learnhub_course_db`) | Gestion du catalogue de formations, catégories, chapitres et ressources pédagogiques | Membre 2 |
| **`learning-journey-service`** | `8083` | MySQL (`learnhub_journey_db`) | Gestion des inscriptions, suivi de progression par leçons et objectifs d'apprentissage | Membre 3 |
| **`assessment-service`** | `8084` | MySQL (`learnhub_assessment_db`) | Conception de quiz, soumission de tentatives de test, calcul de score et émission de certificats | Membre 4 |
| **`marketplace-service`** | `8085` | MySQL (`learnhub_marketplace_db`) | Gestion des paniers, commandes, paiements sandbox et facturation numérique | **Selim Hechmi** |
| **`community-service`** | `8086` | MongoDB / MySQL (`learnhub_community_db`) | Avis vérifiés sur les cours, fils de discussion, modération et badges d'engagement | Membre 5 |
| **`ai-learning-coach-service`** | `8087` | MongoDB / MySQL (`learnhub_aicoach_db`) | *(Technologie avancée)* Génération de plans d'étude personnalisés et assistant de tutorat interactif | Membre 6 |

---

## 🛠️ Stack Technique Backend
- **Langage / Framework :** Java 17+ / Spring Boot 3.4.x
- **Persistance :** Spring Data JPA, Hibernate, H2 (dev/tests autonomes), MySQL 8.0, MongoDB 7.0
- **Documentation API :** Springdoc OpenAPI 3.1 & Swagger UI
- **Communications synchrones :** Spring Cloud OpenFeign avec timeout et gestion de panne (Circuit Breaker)
- **Communications asynchrones :** Spring AMQP / RabbitMQ avec gestion d'idempotence et Dead-Letter Queues (DLQ)
- **Sécurité :** Spring Security, émission/validation de jetons JWT (compatible Keycloak)

---

## 🚀 Démarrage d'un Microservice
Chaque microservice peut être exécuté individuellement :
\`\`\`bash
cd Backend/marketplace-service
mvn clean spring-boot:run
\`\`\`
Une fois démarré, la documentation interactive Swagger est accessible sur :
\`http://localhost:<PORT>/swagger-ui.html\`

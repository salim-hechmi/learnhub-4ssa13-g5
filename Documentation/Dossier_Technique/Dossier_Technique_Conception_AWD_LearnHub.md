# Dossier Technique de Conception & Modélisation Microservices - LearnHub

---

## 🏛️ Informations Administratives & Académiques
- **Établissement :** ESPRIT – École Supérieure Privée d'Ingénierie et de Technologies
- **Année Universitaire :** 2026 – 2027
- **Module :** AWD (Architecture des Applications Web Distribuées)
- **Épreuve :** Mini-projet – Validation 1 : Présentation du projet et modélisation microservices
- **Projet :** **LearnHub** – Plateforme Intelligente d'Apprentissage en Ligne
- **Classe :** 4SSA13 &nbsp;|&nbsp; **Groupe :** G1
- **Étudiants :**
  - **Selim Hechmi** (*Responsable : Marketplace & Billing Service + Co-conception Identity*)
  - **Nawres SOUA, Mohamed Aziz BEN MAMI, Adam NSIRI, Mootez SFAIHI** (*Responsables des microservices métier*)
- **Dépôt Git :** Public avec enseignant ajouté comme collaborateur

---

## 📑 Sommaire
1. [Présentation du Sujet & Analyse Fonctionnelle](#1-présentation-du-sujet--analyse-fonctionnelle)
2. [Acteurs & Cas d'Utilisation](#2-acteurs--cas-dutilisation)
3. [Scénario Métier Principal Transversal](#3-scénario-métier-principal-transversal)
4. [Découpage en Microservices (Bounded Contexts)](#4-découpage-en-microservices-bounded-contexts)
5. [Données & Persistance (Database-per-Service)](#5-données--persistance-database-per-service)
6. [Conception Détaillée par Microservice & Diagrammes de Classes](#6-conception-détaillée-par-microservice--diagrammes-de-classes)
7. [Contrats d'API REST & Spécifications OpenAPI](#7-contrats-dapi-rest--spécifications-openapi)
8. [Communications Interservices : Synchrones (OpenFeign) & Asynchrones (RabbitMQ)](#8-communications-interservices--synchrones-openfeign--asynchrones-rabbitmq)
9. [Architecture Technique Globale & Déploiement](#9-architecture-technique-globale--déploiement)
10. [Sécurité & Contrôle d'Accès (JWT / Keycloak)](#10-sécurité--contrôle-daccès-jwt--keycloak)
11. [Organisation du Dépôt Git & Stratégie de Collaboration](#11-organisation-du-dépôt-git--stratégie-de-collaboration)
12. [Planning Prévisionnel, Gestion des Risques & Mesures Préventives](#12-planning-prévisionnel-gestion-des-risques--mesures-préventives)
13. [Guide de Maîtrise & Réponses aux Questions d'Évaluation](#13-guide-de-maîtrise--réponses-aux-questions-dévaluation)

---

## 1. Présentation du Sujet & Analyse Fonctionnelle

### 1.1. Contexte & Problématique
Le secteur de la formation en ligne et de l'e-learning connaît une expansion rapide. Toutefois, les apprenants sont confrontés à un problème majeur : **l'éparpillement des outils et le cloisonnement des données**. 
Pour monter en compétences, un étudiant doit généralement :
- Acheter une formation sur une boutique e-commerce classique ;
- Suivre des vidéos sur une plateforme de streaming séparée ;
- Réaliser des exercices ou quiz sur un tiers outil d'évaluation ;
- Discuter ou poser des questions sur un forum communautaire ou un réseau social externe ;
- Tenter d'organiser son planning de révision de manière isolée sans accompagnement adaptatif.

Cette fragmentation entraîne une baisse sensible de l'engagement, un abandon précoce des cours (taux de complétion moyen inférieur à 15%) et une incapacité à mesurer de façon continue l'acquisition réelle des compétences.

### 1.2. Solution Proposée : La Plateforme LearnHub
**LearnHub** est une plateforme moderne unifiée conçue pour relier, au sein d'une même expérience utilisateur, l'ensemble du cycle de vie de l'apprentissage :
1. **Un catalogue de formations modulaire :** structuré par catégories, chapitres hiérarchisés et ressources pédagogiques riches (vidéos, supports PDF, liens de code).
2. **Un espace de suivi personnel (« Learning Journey ») :** enregistrant en temps réel la complétion de chaque leçon et calculant un pourcentage de progression précis.
3. **Un centre d'évaluation certifiant :** avec banques de questions, gestion des tentatives multiples, notation automatisée et délivrance de certificats sécurisés.
4. **Une place de marché intégrée (« Marketplace & Billing ») :** permettant la commande, le paiement simulé sécurisé et la génération immédiate de factures numériques.
5. **Un espace communautaire (« Community & Feedback ») :** favorisant l'émulation via des avis vérifiés (réservés aux inscrits), des fils de discussion par cours et l'attribution de badges d'engagement.
6. **Un coach d'apprentissage propulsé par l'IA (« AI Learning Coach ») :** capable de recommander des formations ciblées et de générer un plan de travail sur mesure adapté à la disponibilité de l'étudiant.

### 1.3. Périmètre Réaliste du Projet (Validation 1)
Pour garantir la faisabilité et la qualité académique du projet, les limites suivantes ont été définies :
- **Paiements :** Les paiements passent par un simulateur sandbox transactionnel (aucun débit bancaire réel).
- **Intelligence Artificielle :** Le service IA utilise une API de modèle existant (NLP / LLM) ou une heuristique de recommandation structurée (aucun entraînement lourd de modèle en local).
- **Focus de la Validation 1 :** Validation stricte de la conception logicielle, des contextes bornés (Bounded Contexts), de l'isolation des bases de données et de l'initialisation des projets Spring Boot dans le dépôt Git. La logique métier avancée sera développée dans les phases ultérieures.

---

## 2. Acteurs & Cas d'Utilisation

### 2.1. Tableau des Acteurs
| Acteur | Rôle & Responsabilités dans le Système |
|---|---|
| **Étudiant** | Découvrir et rechercher des formations, commander et payer des cours, suivre ses cours leçon par leçon, passer des quiz, télécharger ses certificats, publier des avis vérifiés, participer aux discussions et échanger avec le coach IA. |
| **Formateur** | Concevoir et publier des formations, structurer les chapitres, attacher des ressources pédagogiques, créer les quiz d'évaluation, répondre aux questions des étudiants et modérer les fils de discussion de ses cours. |
| **Administrateur** | Gérer les comptes utilisateurs et l'attribution des rôles, administrer les catégories globales de formation, superviser l'écosystème et modérer les contenus signalés. |
| **Services Externes** | Passerelle de test sandbox (simulation bancaire), API de modèle IA (NLP/génération de plans d'étude), serveur de gestion d'identité (Keycloak). |

### 2.2. Principaux Cas d'Utilisation par Domaine
- **Gestion des Identités :** S'inscrire, S'authentifier, Consulter son profil, Modifier son profil, Gérer les rôles.
- **Gestion du Catalogue :** Consulter le catalogue, Filtrer par catégorie, Créer un cours, Modifier un cours, Publier un cours, Ajouter un chapitre/ressource.
- **Parcours d'Apprentissage :** Créer une inscription, Visualiser sa progression globale, Valider une leçon terminée, Définir un objectif hebdomadaire.
- **Évaluations & Certifications :** Créer un quiz, Répondre à un quiz, Calculer le score obtenu, Émettre et vérifier un certificat.
- **Marketplace & Facturation :** Créer une commande (panier), Exécuter un paiement simulé, Consulter l'historique des commandes, Télécharger une facture.
- **Communauté & Avis :** Publier un avis noté (1 à 5 étoiles), Créer un fil de discussion, Répondre à un message (post), Épingler/fermer un fil, Consulter ses badges.
- **Coach IA :** Générer un planning de révision personnalisé, Obtenir des recommandations de cours adaptées, Poser des questions au tuteur conversationnel.

---

## 3. Scénario Métier Principal Transversal

Afin de démontrer l'intégration fluide des 7 microservices, nous présentons le scénario d'un étudiant de son inscription jusqu'à l'obtention de son diplôme :
```
[1. Connexion / Auth] ──> identity-service (Émission JWT)
           │
[2. Choix Formation] ───> course-service (Consultation catalogue & prix)
           │
[3. Paiement SandBox] ──> marketplace-service (Validation commande & paiement)
           │
           ├── [ÉVÉNEMENT ASYNCHRONE RABBITMQ : PaymentCompleted]
           │                              │
           │                              ▼
[4. Inscription Auto] ──> learning-journey-service (Création Enrollment & Progress 0%)
           │
[5. Apprentissage] ─────> learning-journey-service (Validation des leçons terminées)
           │
[6. Plan d'Étude IA] ───> ai-learning-coach-service (Génération plan révision)
           │
[7. Passage Quiz] ──────> assessment-service (Score calculé >= seuil de réussite)
           │
           ├── [ÉVÉNEMENT ASYNCHRONE RABBITMQ : CertificateIssued]
           │                              │
           │                              ▼
[8. Certification] ─────> assessment-service (Génération Certificat officiel)
           │                              │
[9. Badge & Avis] ──────> community-service (Attribution Badge de Réussite & Avis)
```

**Valeur Ajoutée AWD :** Ce scénario relie harmonieusement l'ensemble des capacités métier sans jamais bloquer l'interface utilisateur grâce aux événements RabbitMQ.

---

## 4. Découpage en Microservices (Bounded Contexts)

Le découpage applicatif LearnHub respecte rigoureusement la méthodologie du **Domain-Driven Design (DDD)**. Chaque microservice correspond à une frontière métier étanche (*Bounded Context*), garantissant une haute cohésion interne et un couplage externe minimal.

| N° | Nom du Microservice | Port | Bounded Context & Responsabilité Métier | Entités Clés |
|:---:|---|:---:|---|---|
| **0** | **`identity-service`** *(Commun)* | `8081` | Gestion centralisée des utilisateurs, profils, rôles (Étudiant, Formateur, Admin) et validation des jetons JWT. | `User`, `Role`, `UserProfile`, `Permission` |
| **1** | **`course-service`** | `8082` | Catalogue officiel, gestion des cours, structuration en chapitres et ressources d'apprentissage. | `Course`, `Category`, `Chapter`, `LearningResource` |
| **2** | **`learning-journey-service`** | `8083` | Inscriptions aux cours, suivi en temps réel de la progression et validation des leçons terminées. | `Enrollment`, `LearningProgress`, `LessonCompletion`, `LearningGoal` |
| **3** | **`assessment-service`** | `8084` | Banques de quiz et questions, gestion des tentatives de test, calcul des scores et émission de certificats. | `Quiz`, `Question`, `StudentAttempt`, `Certificate` |
| **4** | **`marketplace-service`** *(Selim Hechmi)* | `8085` | Gestion des commandes de formations, traitement des paiements sandbox et facturation numérique. | `Order`, `OrderItem`, `Payment`, `Invoice` |
| **5** | **`community-service`** | `8086` | Avis notés vérifiés, fils de discussion thématiques, modération et attribution de badges de progression. | `CourseReview`, `DiscussionThread`, `Post`, `Badge` |
| **6** | **`ai-learning-coach-service`** *(Avancé)* | `8087` | Génération intelligente de plans d'étude personnalisés et tutorat conversationnel par IA. | `StudyPlan`, `AIRecommendation`, `ChatSession`, `ChatMessage` |

---

## 5. Données & Persistance (Database-per-Service)

### 5.1. Principe Fondamental AWD : Aucune Base Partagée
Conformément aux exigences les plus strictes du module AWD :
- **Chaque microservice dispose de sa propre base de données dédiée.**
- Aucun microservice ne peut exécuter de requête directe (`SELECT`, `INSERT`, `UPDATE`, `JOIN`) sur la base d'un autre service.
- Les liaisons inter-services sont matérialisées par des **identifiants techniques simples** (`Long studentId`, `Long courseId`), jamais par des annotations JPA `@OneToMany` ou `@ManyToOne` pointant sur des tables d'autres services.

### 5.2. Choix Technologiques des Bases de Données
| Microservice | Base Choisie | Justification Technique Approfondie |
|---|:---:|---|
| **`identity-service`** | **MySQL 8.0** | Données relationnelles hautement structurées (comptes, mots de passe hashés, rôles), exigence d'intégrité référentielle stricte. |
| **`course-service`** | **MySQL 8.0** | Structure hiérarchique naturelle et ordonnée (Catégorie $\rightarrow$ Cours $\rightarrow$ Chapitre $\rightarrow$ Ressources). Requêtes de filtrage relationnel simples. |
| **`learning-journey-service`** | **MySQL 8.0** | Contraintes relationnelles fortes sur l'historique de complétion et vérification d'unicité d'une inscription (`studentId`, `courseId`). |
| **`assessment-service`** | **MySQL 8.0** | Structure tabulaire stricte pour lier quiz, questions, options et tentatives de score avec garantie d'intégrité. |
| **`marketplace-service`** | **MySQL 8.0** | **Cohérence ACID impérative :** Les transactions financières (commandes, paiements et factures) exigent une conformité transactionnelle absolue sans perte de données. |
| **`community-service`** | **MongoDB 7.0** *(ou MySQL)* | Schéma semi-structuré idéal pour les fils de discussion imbriqués (threads et posts multiples) et structure dynamique des badges. |
| **`ai-learning-coach-service`** | **MongoDB 7.0** *(ou MySQL)* | Flexibilité maximale pour stocker l'historique des conversations de chat et les plannings d'études JSON générés dynamiquement par l'IA. |

---

## 6. Conception Détaillée par Microservice & Diagrammes de Classes

### 6.1. Microservice 0 : Identity & User Management (`identity-service` - :8081)
- **Rôle :** Service commun développé collectivement. Il constitue le socle de sécurité de LearnHub.
- **Modèle de données :**
  - `User` (`id`, `email`, `password`, `firstName`, `lastName`, `role`, `enabled`, `createdAt`)
  - `UserProfile` (`id`, `userId`, `bio`, `avatarUrl`, `headline`, `linkedInUrl`, `githubUrl`)
  - `Role` (`id`, `name`, `description`)
  - `Permission` (`id`, `code`, `description`)
- **Relations :** `User` 1 — 1 `UserProfile` ; `User` — `Role` ; `Role` — `Permission`.

### 6.2. Microservice 1 : Course Management (`course-service` - :8082)
- **Rôle :** Gestion du catalogue, publication et structure des cours.
- **Modèle de données :**
  - `Category` (`id`, `name`, `slug`, `description`)
  - `Course` (`id`, `title`, `subtitle`, `description`, `instructorId`, `categoryId`, `price`, `published`, `averageRating`, `reviewsCount`, `createdAt`)
  - `Chapter` (`id`, `courseId`, `title`, `orderIndex`)
  - `LearningResource` (`id`, `chapterId`, `title`, `resourceType`, `resourceUrl`, `durationMinutes`)
- **Relations :** `Category` 1 — 1..\* `Course` ; `Course` 1 — 1..\* `Chapter` ; `Chapter` 1 — 1..\* `LearningResource`.

### 6.3. Microservice 2 : Learning Journey (`learning-journey-service` - :8083)
- **Rôle :** Inscription, comptabilisation des leçons terminées et avancement pédagogique.
- **Modèle de données :**
  - `Enrollment` (`id`, `studentId`, `courseId`, `status`, `enrolledAt`)
  - `LearningProgress` (`id`, `enrollmentId`, `completionPercentage`, `completedLessonsCount`, `lastAccessedAt`)
  - `LessonCompletion` (`id`, `progressId`, `resourceId`, `completed`, `completedAt`)
  - `LearningGoal` (`id`, `studentId`, `targetSkill`, `targetHoursPerWeek`, `targetDate`)
- **Relations :** `Enrollment` 1 — 1 `LearningProgress` ; `LearningProgress` 1 — 1..\* `LessonCompletion`. `studentId` et `courseId` sont stockés par référence.

### 6.4. Microservice 3 : Assessment & Certification (`assessment-service` - :8084)
- **Rôle :** Validation des compétences, calcul des scores et génération des diplômes.
- **Modèle de données :**
  - `Quiz` (`id`, `courseId`, `title`, `passingScore`, `maxAttempts`)
  - `Question` (`id`, `quizId`, `text`, `optionA`, `optionB`, `optionC`, `optionD`, `correctOption`, `points`)
  - `StudentAttempt` (`id`, `quizId`, `studentId`, `score`, `passed`, `attemptedAt`)
  - `Certificate` (`id`, `studentAttemptId`, `studentId`, `courseId`, `certificateCode`, `issueDate`, `verificationUrl`)
- **Relations :** `Quiz` 1 — 1..\* `Question` ; `StudentAttempt` 0..1 — 1 `Certificate`.

### 6.5. Microservice 4 : Marketplace & Billing (`marketplace-service` - :8085)
- **Responsable :** **Selim Hechmi**
- **Rôle :** Monétisation des formations, panier, transaction sandbox et émission des factures.
- **Modèle de données :**
  - `Order` (`id`, `studentId`, `totalAmount`, `currency`, `status`, `createdAt`)
  - `OrderItem` (`id`, `orderId`, `courseId`, `price`)
  - `Payment` (`id`, `orderId`, `amount`, `paymentMethod`, `transactionId`, `status`, `paymentDate`)
  - `Invoice` (`id`, `orderId`, `invoiceNumber`, `totalAmount`, `issuedAt`, `downloadUrl`)
- **Relations :** `Order` 1 — 1..\* `OrderItem` ; `Order` 1 — 0..\* `Payment` ; `Order` 1 — 0..1 `Invoice`.

### 6.6. Microservice 5 : Community & Feedback (`community-service` - :8086)
- **Rôle :** Retour d'expérience sur les formations, discussions et reconnaissance sociale.
- **Modèle de données :**
  - `CourseReview` (`id`, `courseId`, `studentId`, `rating`, `comment`, `createdAt`)
  - `DiscussionThread` (`id`, `courseId`, `authorId`, `title`, `content`, `isClosed`, `isPinned`, `createdAt`)
  - `Post` (`id`, `threadId`, `authorId`, `content`, `createdAt`)
  - `Badge` (`id`, `studentId`, `badgeType`, `title`, `iconUrl`, `earnedAt`)
- **Règles de gestion :** 
  - Un étudiant ne peut noter un cours que s'il y est inscrit (vérification synchrone OpenFeign auprès de `learning-journey-service`).
  - Unicité stricte du couple `(courseId, studentId)` pour éviter les spams (sinon code HTTP `409 Conflict`).

### 6.7. Microservice 6 : AI Learning Coach (`ai-learning-coach-service` - :8087)
- **Rôle :** Technologie avancée. Recommandations algorithmiques et assistant conversationnel.
- **Modèle de données :**
  - `StudyPlan` (`id`, `studentId`, `targetObjective`, `weeklyAvailabilityHours`, `recommendedScheduleJson`, `generatedAt`)
  - `AIRecommendation` (`id`, `studentId`, `studyPlanId`, `recommendedCourseId`, `justificationReason`, `priorityLevel`)
  - `ChatSession` (`id`, `studentId`, `topic`, `startedAt`)
  - `ChatMessage` (`id`, `sessionId`, `senderRole`, `messageText`, `sentAt`)

---

## 7. Contrats d'API REST & Spécifications OpenAPI

Chaque microservice expose une API REST standardisée, auto-documentée avec Springdoc OpenAPI 3.1.

| Microservice | Méthode | URI | Code Statut | Description |
|---|:---:|---|:---:|---|
| **Identity** | `POST` | `/api/auth/register` | `201 Created` | Inscription d'un nouvel utilisateur |
| | `POST` | `/api/auth/login` | `200 OK` | Authentification et émission du jeton JWT |
| | `GET` | `/api/users/me` | `200 OK` | Consultation du profil connecté |
| | `GET` | `/api/users/{id}/roles` | `200 OK` | Récupération des rôles d'un utilisateur |
| **Course** | `GET` | `/api/courses` | `200 OK` | Lister les cours disponibles au catalogue |
| | `GET` | `/api/courses/{id}` | `200 OK` / `404` | Détail d'un cours avec chapitres |
| | `POST` | `/api/courses` | `201 Created` | Création d'une formation (Formateur) |
| | `PUT` | `/api/courses/{id}` | `200 OK` / `403` | Modification d'un cours par son propriétaire |
| | `POST` | `/api/courses/{id}/publish` | `200 OK` | Publication officielle dans le catalogue |
| **Learning Journey** | `POST` | `/api/enrollments` | `201 Created` / `409` | Créer une inscription (évite doublon) |
| | `GET` | `/api/students/{id}/enrollments` | `200 OK` | Consulter les formations suivies |
| | `GET` | `/api/enrollments/{id}/progress` | `200 OK` | Statistiques de progression par cours |
| | `PUT` | `/api/lessons/{id}/completion` | `200 OK` | Marquer une ressource comme validée |
| **Assessment** | `POST` | `/api/quizzes` | `201 Created` | Créer un nouveau quiz d'évaluation |
| | `GET` | `/api/quizzes/{id}` | `200 OK` / `404` | Consulter le quiz et ses questions |
| | `POST` | `/api/quizzes/{id}/attempts` | `201 Created` | Soumettre les réponses et calculer le score |
| | `GET` | `/api/certificates/{id}` | `200 OK` / `404` | Consulter le certificat officiel |
| **Marketplace** *(Selim Hechmi)* | `POST` | `/api/orders` | `201 Created` | Valider le panier et créer la commande |
| | `GET` | `/api/orders/{id}` | `200 OK` | Détail de la commande |
| | `POST` | `/api/orders/{id}/payments` | `201 Created` / `400` | Exécuter le paiement simulé |
| | `GET` | `/api/invoices/{id}` | `200 OK` | Consulter et télécharger la facture |
| **Community** | `POST` | `/api/courses/{courseId}/reviews` | `201 Created` / `409` | Publier un avis vérifié sur un cours |
| | `GET` | `/api/courses/{courseId}/reviews` | `200 OK` | Lister les avis et notes d'un cours |
| | `POST` | `/api/courses/{courseId}/threads` | `201 Created` | Créer un fil de discussion |
| | `POST` | `/api/threads/{id}/posts` | `201 Created` | Répondre dans un fil de discussion |
| | `GET` | `/api/students/{id}/badges` | `200 OK` | Consulter les badges de réussite |
| **AI Learning Coach** | `POST` | `/api/study-plans` | `201 Created` | Générer un planning personnalisé |
| | `GET` | `/api/students/{id}/recommendations`| `200 OK` | Recommandations de cours par l'IA |
| | `POST` | `/api/chat/sessions` | `201 Created` | Démarrer une conversation tuteur |
| | `POST` | `/api/chat/sessions/{id}/messages` | `200 OK` | Poser une question et recevoir la réponse |

---

## 8. Communications Interservices : Synchrones (OpenFeign) & Asynchrones (RabbitMQ)

### 8.1. Matrice des Communications
| Interaction | Mode | Technologie | Justification AWD |
|---|:---:|:---:|---|
| **`Community ➔ Journey`** | **Synchrone** | Spring Cloud OpenFeign | Vérifier que l'étudiant est réellement inscrit avant d'autoriser la publication de son avis. |
| **`Journey ➔ Course`** | **Synchrone** | Spring Cloud OpenFeign | S'assurer qu'un cours existe et qu'il est en statut « publié » avant de créer une inscription. |
| **`Coach IA ➔ Journey & Assessment`** | **Synchrone** | Spring Cloud OpenFeign | Récupérer la progression et les notes récentes pour adapter le plan d'étude en direct. |
| **`Marketplace ➔ Journey`** | **Asynchrone** | RabbitMQ (`PaymentCompleted`) | Dès paiement confirmé, créer l'inscription en arrière-plan sans ralentir la commande client. |
| **`Community ➔ Course`** | **Asynchrone** | RabbitMQ (`ReviewPosted`) | Recalculer et mettre à jour la note moyenne du cours dans le catalogue sans bloquer l'auteur. |
| **`Assessment ➔ Community`** | **Asynchrone** | RabbitMQ (`CertificateIssued`) | Dès quiz validé, attribuer le badge de certification à l'étudiant dans l'espace communautaire. |
| **`Tous les services ➔ Identity`** | **Sécurité** | Validation locale JWT | Vérification cryptographique de la signature du jeton sans surcharger le réseau. |

### 8.2. Gestion des Pannes & Résilience
- **Comportement OpenFeign :** Timeout de connexion fixé à 2 secondes. En cas d'indisponibilité du service appelé, un Fallback (Resilience4j) intercepte l'erreur et retourne un statut `503 Service Unavailable` clair et non bloquant.
- **Comportement RabbitMQ :** Les messages sont publiés avec persistance. Si un service consommateur est arrêté, les événements s'accumulent dans sa file dédiée. À son redémarrage, il dépile les messages. Après 3 échecs consécutifs, le message est redirigé vers une **Dead-Letter Queue (DLQ)** pour inspection.
- **Idempotence :** Tout consommateur vérifie préalablement en base si l'opération a déjà été exécutée (ex: si l'inscription existe déjà pour cet `orderId`, elle n'est pas recréée).

---

## 9. Architecture Technique Globale & Déploiement

### 9.1. Composants de l'Architecture
1. **Frontend Web (Port 3000) :** Application cliente responsive.
2. **Spring Cloud Gateway (Port 8080) :** Point d'entrée unique qui route les requêtes vers les microservices selon leurs chemins respectifs.
3. **Netflix Eureka Server (Port 8761) :** Annuaire de services pour la découverte dynamique sans adresses IP en dur.
4. **Spring Cloud Config Server (Port 8888) :** Centralisation des profils de configuration (dev, test, prod).
5. **Keycloak / JWT :** Serveur d'authentification et gestionnaire des habilitations RBAC.
6. **7 Microservices Spring Boot :** Indépendants, conteneurisables et scalables individuellement.
7. **Broker RabbitMQ (Ports 5672 / 15672) :** Échangeur d'événements asynchrones.
8. **Docker & Docker Compose :** Orchestration globale de l'environnement complet.

---

## 10. Sécurité & Contrôle d'Accès (JWT / Keycloak)

- **Principe du Stateless JWT :** Après authentification réussie sur `POST /api/auth/login`, un jeton signé est retourné au client.
- **Transmission :** Le jeton est transmis dans chaque requête HTTP via le header `Authorization: Bearer <TOKEN>`.
- **Contrôle Décentralisé :** L'API Gateway vérifie la validité du jeton et les microservices extraient les `claims` (identifiant utilisateur, email, rôles) sans interroger la base d'Identity à chaque appel.
- **Contrôle d'Accès par Rôles (RBAC) :**
  - `ROLE_STUDENT` : Accès aux cours, leçons, commandes, quiz et avis.
  - `ROLE_INSTRUCTOR` : Création et modification de ses propres cours et quiz.
  - `ROLE_ADMIN` : Droits de modération globale et gestion des utilisateurs.

---

## 11. Organisation du Dépôt Git & Stratégie de Collaboration

### 11.1. Structure Imposée du Dépôt
```
learnhub-4ssa13-g5/
├── Backend/
│   ├── identity-service/         # Service commun (Équipe)
│   ├── course-service/           # Nawres SOUA
│   ├── learning-journey-service/ # Mohamed Aziz BEN MAMI
│   ├── assessment-service/       # Adam NSIRI
│   ├── marketplace-service/      # Selim Hechmi
│   ├── community-service/        # Mootez SFAIHI
│   ├── ai-learning-coach-service/# Adam NSIRI (Service Avancé)
│   └── README.md
├── Frontend/                     # Application cliente Web
├── Documentation/
│   ├── Presentation/             # Diaporama HTML & Guides de speech
│   ├── Diagrams/                 # SVG d'architecture, cas d'utilisation et classes
│   └── Dossier_Technique/        # Ce dossier technique complet
├── docker-compose.yml            # Orchestration des bases et de RabbitMQ
└── README.md                     # Documentation racine du projet
```

### 11.2. Stratégie GitFlow & Suivi de Projet
- **Branche `main` :** Version stable validée pour les rendus.
- **Branche `develop` :** Intégration continue des développements.
- **Branches de fonctionnalités :** Une branche par étudiant (ex: `feature/SELIM-marketplace`).
- **Outil de suivi :** Tableau Kanban GitHub Projects / Trello avec assignation explicite des tâches et suivi de l'avancement.

---

## 12. Planning Prévisionnel, Gestion des Risques & Mesures Préventives

### 12.1. Jalons du Projet
1. **Validation 1 (Semaine 4) :** Modélisation, Bounded Contexts, contrats d'API et dépôts initialisés.
2. **Phase 2 (Semaines 5-6) :** Implémentation de la logique métier dans chaque microservice.
3. **Phase 3 (Semaines 7-8) :** Intégration de l'API Gateway, d'Eureka, de RabbitMQ et sécurisation JWT.
4. **Phase 4 (Semaines 9-10) :** Connexion du Frontend, tests d'intégration complets et démonstration finale.

### 12.2. Analyse des Risques & Mesures d'Atténuation
| Risque Identifié | Impact | Mesure Préventive Appliquée |
|---|:---:|---|
| Incompatibilité des formats d'échange inter-services | Élevé | Définition préalable et figée des contrats d'API et schémas d'événements dès la Validation 1. |
| Défaillance d'un microservice distant | Moyen | Intégration de timeouts courts, fallbacks et gestion de Dead-Letter Queue (DLQ) sous RabbitMQ. |
| Complexité excessive de l'intégration IA | Moyen | Approche itérative : heuristique de recommandation simple en premier lieu, puis connexion à une API NLP externe. |
| Déséquilibre des contributions Git | Élevé | Découpage strict d'un microservice par membre avec suivi des commits individuels réguliers. |

---

## 13. Guide de Maîtrise & Réponses aux Questions d'Évaluation

Pour obtenir la note maximale aux critères individuels (8 points), chaque étudiant maîtrise :
1. **La notion de Bounded Context :** Savoir expliquer pourquoi son microservice est une capacité métier autonome et pourquoi il ne partage pas sa base.
2. **Ses entités et son modèle de données :** Justifier les attributs et associations de son diagramme de classes.
3. **Ses contrats d'API REST :** Connaître par cœur ses URI, ses verbes HTTP et la signification des codes de statut (`200 OK`, `201 Created`, `400 Bad Request`, `403 Forbidden`, `404 Not Found`, `409 Conflict`, `503 Service Unavailable`).
4. **Ses flux d'interaction :** Expliquer avec précision quel service il appelle en OpenFeign et quel événement il publie ou consomme via RabbitMQ.

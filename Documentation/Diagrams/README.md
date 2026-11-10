# Diagrammes d'Architecture et de Conception - LearnHub (Module AWD)

Ce dossier regroupe l'ensemble des diagrammes techniques et modèles UML exigés par la grille d'évaluation AWD (Validation 1).

---

## 📁 Liste des Diagrammes Disponibles

### 1. Architecture Globale
- **Fichier Vectoriel :** [`architecture_globale_awd.svg`](./architecture_globale_awd.svg)
- **Composants illustrés :**
  - **Couche Client :** Application Frontend (Web React / JS, port 3000)
  - **Passerelle & Sécurité :** Spring Cloud API Gateway (port 8080) avec filtres JWT & Keycloak
  - **Découverte de services :** Netflix Eureka Server (port 8761)
  - **Configuration Centralisée :** Spring Cloud Config Server (port 8888)
  - **7 Microservices Métier & Commun :**
    - `identity-service` (:8081)
    - `course-service` (:8082)
    - `learning-journey-service` (:8083)
    - `assessment-service` (:8084)
    - `marketplace-service` (:8085) - *Responsable : Selim Hechmi*
    - `community-service` (:8086)
    - `ai-learning-coach-service` (:8087) - *Technologie Avancée (IA)*
  - **Bases de données dédiées :** 5 instances MySQL indépendantes + 2 instances MongoDB (Pattern *Database-per-Service*)
  - **Communication Asynchrone :** Broker RabbitMQ (port 5672) avec échanges et files pour événements métier (`PaymentCompleted`, `ReviewPosted`, `CertificateIssued`)
  - **Conteneurisation :** Déploiement global sous Docker Compose

---

### 2. Diagramme de Cas d'Utilisation Global
- **Fichier Vectoriel :** [`cas_utilisation_global.svg`](./cas_utilisation_global.svg)
- **Acteurs :**
  - **Étudiant :** Découvrir le catalogue, acheter une formation, suivre sa progression leçon par leçon, passer les quiz d'évaluation, télécharger son certificat, publier un avis vérifié, échanger sur les forums et solliciter le coach IA.
  - **Formateur :** Créer des cours, structurer les chapitres, attacher des ressources pédagogiques, concevoir les quiz et suivre l'engagement.
  - **Administrateur :** Modération des avis/posts, gestion des rôles et supervision globale.
  - **Services Externes :** Passerelle de paiement de test (Sandbox) et API de modèle IA.

---

### 3. Diagrammes d'Interactions & Séquences
- **Fichier Vectoriel :** [`sequence_paiement_inscription.svg`](./sequence_paiement_inscription.svg)
- **Scénario Asynchrone Découplé :**
  1. L'étudiant valide son panier via `POST /api/orders/{id}/payments` auprès de `marketplace-service`.
  2. `marketplace-service` enregistre le paiement, génère la facture et publie instantanément l'événement **`PaymentCompleted`** sur le broker RabbitMQ.
  3. `marketplace-service` répond immédiatement au frontend avec le code HTTP **`201 Created`** (aucune attente, fluidité maximale).
  4. `learning-journey-service` consomme l'événement depuis sa file RabbitMQ, vérifie l'idempotence et instancie l'entité `Enrollment` ainsi que la `LearningProgress` initiale à 0%.
  5. *Résilience garantie :* Si `learning-journey-service` est momentanément hors ligne, le message reste stocké dans la file RabbitMQ et sera traité dès le rétablissement du service sans aucune perte de commande.

---

### 4. Diagrammes de Classes par Microservice (Dossier `diagrammes_de_classes/`)
Chaque diagramme est isolé dans son contexte borné, conformément à la règle d'or AWD (*Aucune relation JPA entre bases de données distinctes, uniquement des identifiants distants `Long courseId`, `Long studentId`*) :
- [`identity_class_diagram.svg`](./diagrammes_de_classes/identity_class_diagram.svg) : `User` (1) — (1) `UserProfile` ; `User` — `Role` — `Permission`
- [`course_class_diagram.svg`](./diagrammes_de_classes/course_class_diagram.svg) : `Category` (1) — (1..\*) `Course` (1) — (1..\*) `Chapter` (1) — (1..\*) `LearningResource`
- [`learning_journey_class_diagram.svg`](./diagrammes_de_classes/learning_journey_class_diagram.svg) : `Enrollment` (1) — (1) `LearningProgress` (1) — (1..\*) `LessonCompletion` ; `LearningGoal`
- [`assessment_class_diagram.svg`](./diagrammes_de_classes/assessment_class_diagram.svg) : `Quiz` (1) — (1..\*) `Question` ; `StudentAttempt` (0..1) — (1) `Certificate`
- [`marketplace_class_diagram.svg`](./diagrammes_de_classes/marketplace_class_diagram.svg) : `Order` (1) — (1..\*) `OrderItem` ; `Order` (1) — (0..\*) `Payment` ; `Order` (1) — (0..1) `Invoice`
- [`community_class_diagram.svg`](./diagrammes_de_classes/community_class_diagram.svg) : `DiscussionThread` (1) — (1..\*) `Post` ; `CourseReview` ; `Badge`
- [`ai_coach_class_diagram.svg`](./diagrammes_de_classes/ai_coach_class_diagram.svg) : `StudyPlan` (1) — (1..\*) `AIRecommendation` ; `ChatSession` (1) — (1..\*) `ChatMessage`

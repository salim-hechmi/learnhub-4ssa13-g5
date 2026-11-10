# LearnHub – Plateforme Intelligente d'Apprentissage en Ligne

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.x-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2024.x-blue.svg)](https://spring.io/projects/spring-cloud)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg?logo=docker)](https://www.docker.com/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-AMQP-FF6600.svg?logo=rabbitmq)](https://www.rabbitmq.com/)
[![License](https://img.shields.io/badge/Module-AWD_Validation_1-crimson.svg)](#)

> **Projet Académique ESPRIT – Année Universitaire 2026/2027**  
> **Module :** Architecture des Applications Web Distribuées (AWD)  
> **Classe :** 4SSA13 &nbsp;|&nbsp; **Groupe :** G1  
> **Validation 1 :** Présentation du projet et modélisation microservices (Semaine Séance 4)

---

## 👥 Équipe & Responsabilités par Microservice

| Membre | Microservice Attribué | Rôle & Responsabilité Métier | Base de Données |
|---|---|---|---|
| **Selim Hechmi** *(salim-hechmi)* | **`marketplace-service`** &amp; Co-dev `identity-service` | Commandes, panier, transactions sandbox de test, facturation et émission de l'événement `PaymentCompleted` | MySQL (`learnhub_marketplace_db`) |
| **Membre 2** | **`course-service`** | Catalogue officiel, cours, chapitres hiérarchisés et ressources pédagogiques | MySQL (`learnhub_course_db`) |
| **Membre 3** | **`learning-journey-service`** | Inscriptions, avancement pas à pas, complétion des leçons et objectifs | MySQL (`learnhub_journey_db`) |
| **Membre 4** | **`assessment-service`** | Quiz, banques de questions, calcul automatique des scores et diplômes certifiants | MySQL (`learnhub_assessment_db`) |
| **Membre 5** | **`community-service`** | Avis notés vérifiés (réservés aux inscrits), forums de discussion et badges | MongoDB (`learnhub_community_db`) |
| **Membre 6** | **`ai-learning-coach-service`** *(Avancé)* | *(Technologie avancée)* Plans d'étude personnalisés et tutorat conversationnel par IA | MongoDB (`learnhub_aicoach_db`) |
| **Toute l'équipe** | **`identity-service`** *(Socle commun)* | Authentification sécurisée par jeton JWT, profils, permissions et rôles | MySQL (`learnhub_identity_db`) |

---

## 🎯 Problématique & Solution

- **Problématique :** Les apprenants en ligne sont confrontés à une dispersion importante de leurs outils : plateformes vidéos cloisonnées, exercices sur des sites tiers, absence de suivi centralisé et manque d'accompagnement interactif.
- **Solution LearnHub :** Une plateforme intégrée, hautement modulaire, reliant dans un même parcours utilisateur : catalogue structuré, apprentissage progressif, évaluations certifiantes, paiement en ligne, reconnaissance communautaire et accompagnement intelligent par un coach IA.

---

## 🏛️ Architecture Technique Globale

L'architecture repose sur l'écosystème **Spring Cloud** et respecte scrupuleusement la stack imposée pour le module AWD :

```
                               ┌────────────────────────────────┐
                               │   Application Web Frontend     │ (Port 3000)
                               └───────────────┬────────────────┘
                                               │ HTTP / REST
                                               ▼
                               ┌────────────────────────────────┐
                               │   Spring Cloud API Gateway     │ (Port 8080)
                               │  Routage dynamique & Filtre JWT│
                               └───────────────┬────────────────┘
                                               │
            ┌──────────────────────────────────┴──────────────────────────────────┐
            ▼                                                                     ▼
┌───────────────────────┐                                             ┌───────────────────────┐
│ Netflix Eureka Server │ (:8761 - Découverte de services)            │ Keycloak / Auth JWT   │
└───────────────────────┘                                             └───────────────────────┘
            │                                                                     │
 ───────────┴─────────────────────────────────────────────────────────────────────┴───────────
                                      7 MICROSERVICES SPRING BOOT
 ─────────────────────────────────────────────────────────────────────────────────────────────
  [identity-service]    (:8081) ── MySQL (learnhub_identity_db)
  [course-service]      (:8082) ── MySQL (learnhub_course_db)
  [journey-service]     (:8083) ── MySQL (learnhub_journey_db)
  [assessment-service]  (:8084) ── MySQL (learnhub_assessment_db)
  [marketplace-service] (:8085) ── MySQL (learnhub_marketplace_db)  <-- Selim Hechmi
  [community-service]   (:8086) ── MongoDB (learnhub_community_db)
  [ai-coach-service]    (:8087) ── MongoDB (learnhub_aicoach_db)      <-- Technologie Avancée (IA)
 ─────────────────────────────────────────────────────────────────────────────────────────────
                                 COMMUNICATIONS INTERSERVICES
   * Synchrones (OpenFeign) : Vérifications immédiates (ex: Inscription vérifiée avant avis)
   * Asynchrones (RabbitMQ) : Événements métier découplés (PaymentCompleted, ReviewPosted, etc.)
   * Conteneurisation       : Docker & Docker Compose
```

---

## 📁 Structure du Dépôt Git

Le dépôt respecte rigoureusement l'organisation demandée par l'enseignant :

```text
learnhub-4ssa13-g1/
├── Backend/
│   ├── identity-service/          # Service commun d'authentification et utilisateurs (:8081)
│   ├── course-service/            # Microservice Catalogue et cours (:8082)
│   ├── learning-journey-service/  # Microservice Inscriptions et progression (:8083)
│   ├── assessment-service/        # Microservice Quiz et certifications (:8084)
│   ├── marketplace-service/       # Microservice Commandes, paiements et factures (:8085)
│   ├── community-service/         # Microservice Avis, forums et badges (:8086)
│   ├── ai-learning-coach-service/ # Microservice Avancé IA de tutorat (:8087)
│   └── README.md
├── Frontend/                      # Application cliente Web (Port 3000)
├── Documentation/
│   ├── Presentation/              # Support de présentation séance 4 & scripts oraux
│   │   ├── LearnHub_Presentation_Validation1.html
│   │   └── Guide_Oral_Speech_Validation1.md
│   ├── Diagrams/                  # Diagrammes vectoriels SVG & documentation
│   │   ├── architecture_globale_awd.svg
│   │   ├── cas_utilisation_global.svg
│   │   ├── sequence_paiement_inscription.svg
│   │   └── diagrammes_de_classes/ (1 diagramme de classes par microservice)
│   └── Dossier_Technique/         # Dossier technique complet (Markdown, HTML imprimable, LaTeX)
│       ├── Dossier_Technique_Conception_AWD_LearnHub.md
│       ├── Dossier_Technique_Conception_AWD_LearnHub.html
│       └── Dossier_Technique_Conception_AWD_LearnHub.tex
├── docker-compose.yml             # Orchestration locale Docker (Bases MySQL, MongoDB, RabbitMQ)
└── README.md                      # Présentation générale du projet
```

---

## ⚡ Principes Clés AWD Appliqués

1. **Un microservice par capacité métier (Bounded Context) :** Aucun découpage artificiel par table ou par écran.
2. **Database-per-Service :** Chaque microservice est le propriétaire exclusif de sa base de données. Aucune base n'est partagée.
3. **Découplage temporel via RabbitMQ :** 
   - L'événement `PaymentCompleted` (émis par Marketplace) permet à `learning-journey-service` de créer l'inscription de façon asynchrone et idempotente, sans bloquer la commande de l'étudiant.
4. **Appels synchrones maîtrisés via OpenFeign :**
   - Requêtes ciblées de lecture (ex: vérification de publication de cours ou d'inscription active) protégées par un timeout court (2s) et une gestion de repli (Circuit Breaker).
5. **Sécurité Décentralisée JWT :**
   - Jetons émis par le service d'identité et validés localement par chaque microservice via leurs rôles (`ROLE_STUDENT`, `ROLE_INSTRUCTOR`, `ROLE_ADMIN`).

---

## 🚀 Guide de Démarrage Rapide

### Prérequis
- **JDK 17** ou supérieur installé
- **Apache Maven 3.8+**
- **Docker & Docker Compose** (pour l'infrastructure MySQL / RabbitMQ)
- **Node.js 18+** (pour l'application cliente Frontend)

### 1. Démarrer l'infrastructure (Bases & Message Broker)
\`\`\`bash
docker-compose up -d
\`\`\`

### 2. Démarrer un microservice Backend (Exemple : Marketplace)
\`\`\`bash
cd Backend/marketplace-service
mvn clean spring-boot:run
\`\`\`
- Documentation Swagger interactive : [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)
- Console base H2 (mode dev immédiat) : [http://localhost:8085/h2-console](http://localhost:8085/h2-console)

### 3. Démarrer le Frontend
\`\`\`bash
cd Frontend
npm install
npm start
\`\`\`
- Interface Web accessible sur : [http://localhost:3000](http://localhost:3000)

---

## 📊 Suivi de Projet & Méthodologie Git

- **Modèle de Branches :** GitFlow
  - `main` : Version livrée et validée.
  - `develop` : Intégration continue.
  - Branches nominatives de travail : `feature/SELIM-marketplace`, etc.
- **Outil de suivi :** Tableau Kanban GitHub Projects / Trello avec découpage des user stories, critères d'acceptation et statuts (À faire, En cours, Revue, Terminé).
- **Rendu Blackboard :** Dépôt Git configuré en visibilité publique avec ajout de l'enseignant en tant que collaborateur.

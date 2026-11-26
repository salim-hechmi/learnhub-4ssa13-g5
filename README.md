# LearnHub – Plateforme Intelligente d'Apprentissage en Ligne

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.x-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2024.x-blue.svg)](https://spring.io/projects/spring-cloud)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg?logo=docker)](https://www.docker.com/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-AMQP-FF6600.svg?logo=rabbitmq)](https://www.rabbitmq.com/)
[![License](https://img.shields.io/badge/Module-AWD_Validation_1-crimson.svg)](#)

> **Projet Académique ESPRIT – Année Universitaire 2026/2027**  
> **Module :** Architecture des Applications Web Distribuées (AWD)  
> **Classe :** 4SSA13 &nbsp;|&nbsp; **Groupe :** G5  
> **Validation 1 :** Présentation du projet et modélisation microservices (Semaine Séance 4)

---

## 👥 Équipe & Responsabilités par Microservice

| Membre | Microservice Attribué | Rôle & Responsabilité Métier | Base de Données |
|---|---|---|---|
| **Selim Hechmi** *(salim-hechmi)* | **`marketplace-service`** &amp; Co-dev `identity-service` | Commandes, panier, transactions sandbox de test, facturation et émission de l'événement `PaymentCompleted` | MySQL (`learnhub_marketplace_db`) |
| **Nawres SOUA** | **`course-service`** | Catalogue officiel, cours, chapitres hiérarchisés et ressources pédagogiques | MySQL (`learnhub_course_db`) |
| **Mohamed Aziz BEN MAMI** | **`learning-journey-service`** | Inscriptions, avancement pas à pas, complétion des leçons et objectifs | MySQL (`learnhub_journey_db`) |
| **Adam NSIRI** | **`assessment-service`** | Quiz, banques de questions, calcul automatique des scores et diplômes certifiants | MySQL (`learnhub_assessment_db`) |
| **Mootez SFAIHI** | **`community-service`** | Avis notés vérifiés (réservés aux inscrits), forums de discussion et badges | MongoDB (`learnhub_community_db`) |
| **Adam NSIRI** | **`ai-learning-coach-service`** *(Avancé)* | *(Technologie avancée)* Plans d'étude personnalisés et tutorat conversationnel par IA | MongoDB (`learnhub_aicoach_db`) |
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
learnhub-4ssa13-g5/
├── Backend/
│   ├── api-gateway/               # Point d'accès unique, Swagger unifié, load balancing, circuit breaker (:8080)
│   ├── eureka-server/             # Découverte de services, 2 instances répliquées (:8761 / :8762)
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

## 🛰️ Découverte de services (Eureka) & Observabilité (Actuator)

### Dashboard Eureka

![Dashboard Eureka 8761](Documentation/Captures/eureka-dashboard-8761.png)

- **Deux serveurs Eureka** répliqués : [http://localhost:8761](http://localhost:8761) et [http://127.0.0.1:8762](http://127.0.0.1:8762). Chacun réplique son registre vers l'autre (*DS Replicas* et *available-replicas* sur la capture), donc les deux affichent les mêmes services. Capture du second serveur : [eureka-dashboard-8762.png](Documentation/Captures/eureka-dashboard-8762.png).
- **Les deux microservices les plus sollicités sont dupliqués** et visibles en même temps :
  - `course-service` (Nawres SOUA) : ports `8082` et `8092` ;
  - `marketplace-service` (Selim Hechmi) : ports `8085` et `8095`.
- Les microservices déclarent les **deux** serveurs dans `eureka.client.service-url.defaultZone` : si l'un tombe, ils restent visibles sur l'autre.
- Détails et marche à suivre pour rendre un service client Eureka : [Backend/eureka-server/README.md](Backend/eureka-server/README.md).

### Intégration marketplace-service ↔ course-service

Avant de créer une commande, le marketplace demande le prix et le statut du cours à `course-service`, **sans connaître son adresse** :

```
marketplace-service ──(1) « où est course-service ? »──► Eureka (8761 / 8762)
        │                                               └─► course-service:8082 , course-service:8092
        └──(2) GET /api/courses/{id}/pricing (OpenFeign + load balancer) ──► l'une des deux instances
```

- Un cours **publié** est vendu au prix renvoyé par course-service. Un cours en **brouillon** est refusé (400).
- Les appels sont **répartis entre les deux instances** de course-service. Exemple après 6 commandes : 5 appels traités par 8082 et 3 par 8092 (métrique `http.server.requests`, captures [8082](Documentation/Captures/loadbalancing-course-8082.png) · [8092](Documentation/Captures/loadbalancing-course-8092.png)).
- Si course-service ne répond pas, un circuit breaker (Resilience4j) renvoie un 503 clair au lieu de bloquer.

### Lancer l'architecture sur un PC du groupe

Prérequis : Java 17+ et Maven. Pour le mode `integration`, MySQL doit tourner sur le port 3306 (Laragon / XAMPP, utilisateur `root` sans mot de passe). Les bases `db_marketplace` et `learnhub_course_db` sont créées automatiquement.

```powershell
cd Backend
powershell -ExecutionPolicy Bypass -File .\start-all.ps1               # 2 Eureka + 2 course + 2 marketplace + Gateway
powershell -ExecutionPolicy Bypass -File .\start-all.ps1 -Mode local   # 2 Eureka + 2 marketplace + Gateway, sans MySQL
.\stop-all.ps1                                                           # tout arrêter
```

Pour tester l'intégration : créer un cours dans course-service (Swagger : http://localhost:8082/swagger-ui.html), le publier (`PUT /api/courses/{id}/publish`), puis le commander dans le marketplace (Swagger : http://localhost:8085/swagger-ui.html, `POST /api/orders` avec `"courseIds": [id]`).

### Endpoints Actuator exposés

`health`, `info`, `metrics`, `env`, `beans`, `mappings`, `loggers`, `configprops`, `threaddump` (sur `/actuator/...`).

| Endpoint | Capture |
|---|---|
| `/actuator` (liste des endpoints) | [actuator-index-8085.png](Documentation/Captures/actuator-index-8085.png) |
| `/actuator/health` (instance 8085, instance 8095) | [8085](Documentation/Captures/actuator-health-8085.png) · [8095](Documentation/Captures/actuator-health-8095.png) |
| `/actuator/info` | [actuator-info-8085.png](Documentation/Captures/actuator-info-8085.png) |
| `/actuator/metrics` | [actuator-metrics-8085.png](Documentation/Captures/actuator-metrics-8085.png) |
| `/actuator/metrics/http.server.requests` | [actuator-metrics-http-requests-8085.png](Documentation/Captures/actuator-metrics-http-requests-8085.png) |
| `/actuator/metrics/jvm.memory.used` | [actuator-metrics-jvm-memory-8085.png](Documentation/Captures/actuator-metrics-jvm-memory-8085.png) |
| `/actuator/env` | [actuator-env-8085.png](Documentation/Captures/actuator-env-8085.png) |
| `/actuator/mappings` | [actuator-mappings-8085.png](Documentation/Captures/actuator-mappings-8085.png) |
| `/actuator/loggers` | [actuator-loggers-8085.png](Documentation/Captures/actuator-loggers-8085.png) |
| `/actuator/health` du serveur Eureka | [actuator-eureka-health-8761.png](Documentation/Captures/actuator-eureka-health-8761.png) |
| `/actuator/health` de course-service (8082, 8092) | [8082](Documentation/Captures/course-health-8082.png) · [8092](Documentation/Captures/course-health-8092.png) |
| `/api/courses/1/pricing` (contrat lu par le marketplace) | [course-pricing-8082.png](Documentation/Captures/course-pricing-8082.png) |

---

## 🚪 API Gateway : point d'accès unique, Swagger unifié, load balancing et circuit breaker

Code : [Backend/api-gateway](Backend/api-gateway) (Spring Cloud Gateway, port **8080**). Détails : [Backend/api-gateway/README.md](Backend/api-gateway/README.md).

```
Postman / Frontend ──► API Gateway :8080 ──(« où est marketplace-service ? »)──► Eureka 8761 / 8762
                              │
                              ├─ /api/orders/**, /api/payments/**, /api/invoices/** ─► lb://marketplace-service  (Weighted + circuit breaker)
                              ├─ /api/courses/**, /api/categories/**, …             ─► lb://course-service       (Random)
                              └─ identity, learning-journey, assessment, community, ai-coach ─► lb://<service>   (Round Robin)
```

### 1. La Gateway est enregistrée dans Eureka

![Eureka avec la Gateway](Documentation/Captures/eureka-dashboard-gateway-8761.png)

`API-GATEWAY` (8080) apparaît avec les 2 Eureka (8761, 8762), les 2 `course-service` (8082, 8092) et les 2 `marketplace-service` (8085, 8095).

### 2. Point d'accès unique : tout passe par le port 8080

Chaque route utilise `lb://nom-du-service` : la Gateway demande l'adresse à Eureka et ne connaît aucun port de microservice. Les 7 routes sont visibles sur `GET /actuator/gateway/routes` ([capture](Documentation/Captures/gateway-routes.png)).

| Microservice | Endpoint testé **via la Gateway** | Résultat |
|---|---|---|
| marketplace-service | `GET http://localhost:8080/api/orders/stats` | 200 ([capture](Documentation/Captures/gateway-test-marketplace-8080.png)) |
| course-service | `GET http://localhost:8080/api/courses` | 200 ([capture](Documentation/Captures/gateway-test-course-8080.png)) |
| identity-service | `GET http://localhost:8080/api/me` | route prête : le service doit devenir client Eureka |
| learning-journey-service | `GET http://localhost:8080/api/students/{id}/enrollments` | route prête : le service doit devenir client Eureka |
| assessment-service | `GET http://localhost:8080/api/quizzes/1` | route prête : le service doit devenir client Eureka |
| community-service | `GET http://localhost:8080/api/courses/1/reviews` | route prête : le service doit devenir client Eureka |
| ai-learning-coach-service | `GET http://localhost:8080/api/students/{id}/recommendations` | route prête : le service doit devenir client Eureka |

Collection Postman : [Backend/api-gateway/postman/LearnHub-Gateway.postman_collection.json](Backend/api-gateway/postman/LearnHub-Gateway.postman_collection.json) (un dossier par étape : Eureka, un endpoint par microservice, load balancing, circuit breaker). Chaque responsable ajoute la capture Postman de son microservice une fois celui-ci enregistré dans Eureka (dépendance `spring-cloud-starter-netflix-eureka-client` + bloc `eureka:` du yml, voir [Backend/eureka-server/README.md](Backend/eureka-server/README.md)).

### 3. Documentation centralisée : Swagger unifié

**http://localhost:8080/swagger-ui.html** : la liste « Select a definition » contient les 7 microservices. La Gateway récupère la doc OpenAPI de chacun via Eureka (`/docs/<ms>/v3/api-docs`), et « Try it out » envoie les requêtes **à la Gateway** (serveur `http://localhost:8080`).

![Swagger unifié, liste des microservices](Documentation/Captures/gateway-swagger-unifie-liste-ms.png)

Captures complètes : [marketplace-service](Documentation/Captures/gateway-swagger-unifie-marketplace.png) · [course-service](Documentation/Captures/gateway-swagger-unifie-course.png).

### 4. Algorithmes de load balancing (un par microservice)

Configuration : [LoadBalancingConfig.java](Backend/api-gateway/src/main/java/tn/esprit/learnhub/gateway/loadbalancer/LoadBalancingConfig.java). Les autres services gardent l'algorithme par défaut (Round Robin).

| Microservice | Algorithme | Principe | Mesure (20 appels via la Gateway) |
|---|---|---|---|
| course-service (8082, 8092) | **Random** | chaque requête part sur une instance tirée au hasard | 1ʳᵉ série : 5 / 15 ; 2ᵉ série : 9 / 11 |
| marketplace-service (8085, 8095) | **Weighted** (pondéré) | chaque instance annonce un poids dans Eureka (`metadata-map.weight` : 8085 = 3, 8095 = 1) | 15 / 5 à chaque série (75 % / 25 %) |

Nombre de requêtes reçues par chaque instance (`/actuator/metrics/http.server.requests`) : [8085](Documentation/Captures/gateway-lb-weighted-8085.png) · [8095](Documentation/Captures/gateway-lb-weighted-8095.png) · [8082](Documentation/Captures/gateway-lb-random-8082.png) · [8092](Documentation/Captures/gateway-lb-random-8092.png).

### 5. Circuit breaker sur la route du marketplace

Dépendance `spring-cloud-starter-circuitbreaker-reactor-resilience4j`, filtre `CircuitBreaker` sur la route `marketplace-service` avec `fallbackUri: forward:/fallback/marketplace` ([FallbackController.java](Backend/api-gateway/src/main/java/tn/esprit/learnhub/gateway/fallback/FallbackController.java)).

Test réalisé : les deux instances du marketplace sont **arrêtées**, puis `GET http://localhost:8080/api/orders/stats` est appelé plusieurs fois.

1. Les premiers appels échouent (connexion impossible, environ 2 s chacun) : la Gateway renvoie le **fallback** en 503.
2. Dès que le taux d'échec dépasse 50 % (sur 5 appels minimum), le circuit passe à **OPEN** : la Gateway répond par le fallback en **moins de 10 ms**, sans appeler le service (`CallNotPermittedException`).
3. Pendant ce temps, `course-service` reste accessible via la Gateway (200).
4. Après le redémarrage du marketplace, le circuit passe à HALF_OPEN puis revient à **CLOSED** au bout de 15 s.

![Fallback du circuit breaker](Documentation/Captures/gateway-circuitbreaker-fallback.png)

État du circuit : [gateway-circuitbreaker-open.png](Documentation/Captures/gateway-circuitbreaker-open.png) (`/actuator/circuitbreakers`).

---

## 📊 Suivi de Projet & Méthodologie Git

- **Modèle de Branches :** GitFlow
  - `main` : Version livrée et validée.
  - `develop` : Intégration continue.
  - Branches nominatives de travail : `feature/SELIM-marketplace`, etc.
- **Outil de suivi :** Tableau Kanban GitHub Projects / Trello avec découpage des user stories, critères d'acceptation et statuts (À faire, En cours, Revue, Terminé).
- **Rendu Blackboard :** Dépôt Git configuré en visibilité publique avec ajout de l'enseignant en tant que collaborateur.

# Guide Pratique de l'Oral & Scripts de Présentation - Validation 1 AWD

> **Module :** Architecture des Applications Web (AWD)  
> **Session :** Séance 4 (Validation 1 : Présentation du projet et modélisation microservices)  
> **Durée totale recommandée :** 10 à 15 minutes d'exposé + 10 minutes de questions  
> **Note :** 20/20 (Partie Équipe : 12 pts | Partie Individuelle : 8 pts)

---

## ⏱️ Répartition du Temps & Ordre de Passage (13 à 15 min)

| Étape | Slide(s) | Contenu | Intervenant | Durée |
|---|:---:|---|---|:---:|
| **1. Introduction & Contexte** | Slides 1 - 2 | Page de garde, problème réel et solution globale | **Nawres SOUA** | **1 min 20 s** |
| **2. Modélisation Fonctionnelle** | Slides 3 - 4 | Acteurs, cas d'utilisation et scénario transversal | **Mohamed Aziz BEN MAMI** | **1 min 45 s** |
| **3. Décomposition Microservices** | Slide 5 | Découpage en 7 Bounded Contexts et responsabilités | **Adam NSIRI** | **1 min** |
| **4. Architecture & Données** | Slides 6 - 7 | Schéma technique Spring Cloud et Database-per-service | **Selim Hechmi** | **1 min 45 s** |
| **5. Communications & Événements** | Slide 8 | Sync (OpenFeign) vs Async (RabbitMQ) & Résilience | **Mootez SFAIHI** | **1 min** |
| **6. Zoom Individuel - Socle Commun** | Slide 5 / 9a | Microservice `identity-service` (port 8081) | **Présenté par un membre** | **1 min** |
| **7. Zoom Individuel - Marketplace** | Slide 9b | Microservice `marketplace-service` (port 8085) | **Selim Hechmi** | **1 min** |
| **8. Zooms Individuels - Autres Services** | Slides 9c-9f | Course, Journey, Assessment, Community, AI Coach | **Chaque responsable** | **1 min / étudiant** |
| **9. Technologies & Sécurité** | Slide 10 | Stack Spring Boot 3, Keycloak, JWT et Docker | **Adam NSIRI** | **45 s** |
| **10. Organisation Git & Planning** | Slides 11 - 12 | GitFlow, branches, commits, risques et conclusion | **Tous / Membre désigné** | **1 min 45 s** |

---

## 🎤 Scripts Oraux Détaillés (Word-for-Word)

### Slide 1 & 2 : Introduction, Problème & Solution (1 min 20 s)
*« Bonjour Monsieur / Madame. Aujourd’hui, notre équipe a le plaisir de vous présenter la conception et la modélisation microservices de notre projet LearnHub, dans le cadre du module Architecture des Applications Web.*

*Le problème que nous résolvons est concret : actuellement, les étudiants qui apprennent en ligne doivent jongler entre des plateformes séparées pour acheter des formations, suivre des cours vidéo, passer des quiz et poser leurs questions. Il leur manque un espace unifié reliant contenu, suivi de progression, évaluation, communauté et tutorat personnalisé.*

*LearnHub est une plateforme d’apprentissage intelligente et complète. Elle intègre un catalogue modulaire, un espace de suivi personnalisé, un système d'évaluation certifiante, une place de marché, un réseau d'échange communautaire et un coach IA interactif. Pour bâtir ce système sans créer un monolithe fragile, nous avons opté dès le départ pour une architecture microservices distribuée et hautement résiliente. »*

---

### Slide 3 & 4 : Acteurs & Scénario Transversal (1 min 45 s)
*« Notre système s'articule autour de trois acteurs humains :*
- *L'**Étudiant**, qui explore le catalogue, achète ses formations, valide ses leçons pas à pas, passe des quiz certifiants, donne son avis et échange avec la communauté.*
- *Le **Formateur**, qui structure ses cours en chapitres et ressources, crée les évaluations et suit l'activité pédagogique.*
- *L'**Administrateur**, qui gère les rôles, les catégories et la modération des avis et discussions.*
- *S'y ajoutent nos services externes : la passerelle de test pour les paiements sandbox et le moteur d'IA.*

*Pour illustrer la synergie entre nos services, voici notre scénario de référence :*
1. *L'étudiant se connecte via notre service d'identité et obtient un jeton JWT.*
2. *Il sélectionne une formation et valide son panier dans notre microservice Marketplace.*
3. *Dès confirmation du paiement, un événement asynchrone `PaymentCompleted` est publié sur RabbitMQ.*
4. *Sans bloquer l'acheteur, le microservice Learning Journey consomme cet événement et initialise l'inscription et la progression à 0%.*
5. *L'étudiant progresse dans ses leçons, reçoit un plan de travail personnalisé de notre coach IA, passe son quiz d'évaluation et obtient son certificat officiel.*
6. *Enfin, notre service Community lui attribue un badge d'accomplissement et lui permet de déposer un avis vérifié. »*

---

### Slide 5, 6 & 7 : Découpage, Architecture Globale & Données (2 min 45 s)
*« Notre découpage ne suit ni les écrans ni une simple table de base de données : il suit rigoureusement les capacités métier, c'est-à-dire les Bounded Contexts du Domain-Driven Design (DDD).*

*Nous avons 7 microservices Spring Boot au total :*
- *1 service commun : `identity-service` (authentification JWT, utilisateurs et rôles).*
- *6 services métier : `course-service`, `learning-journey-service`, `assessment-service`, `marketplace-service`, `community-service` et notre service de technologie avancée `ai-learning-coach-service`.*

*Au niveau de l'architecture technique :*
- *Le client Frontend communique exclusivement avec notre **Spring Cloud API Gateway** sur le port 8080.*
- *La Gateway assure le filtrage des requêtes, le contrôle des jetons JWT et le routage dynamique.*
- *Le serveur **Netflix Eureka** sur le port 8761 permet à tous les microservices de s'enregistrer et de se découvrir mutuellement sans adresse IP codée en dur.*
- *Pour les données, nous respectons le principe fondamental AWD : **une base dédiée par microservice**, sans aucun partage de tables. 5 services s'appuient sur MySQL pour l'intégrité relationnelle et transactionnelle, et nos services communautaire et IA s'appuient sur MongoDB pour la flexibilité des documents imbriqués. Toute liaison avec une entité extérieure se fait par identifiant simple (ex: `courseId`, `studentId`), garantissant un découplage absolu. »*

---

### Slide 8 : Communications Synchrones vs Asynchrones (1 min)
*« Pour faire communiquer nos services, nous avons appliqué une règle de sélection stricte :*

*1. **La communication synchrone via Spring Cloud OpenFeign** est réservée aux cas où une validation immédiate est requise avant d'exécuter une action. Par exemple, lorsque Community reçoit un avis, il appelle Journey en OpenFeign pour vérifier en temps réel que l'étudiant est bien inscrit. Si Journey ne répond pas, nous appliquons un timeout court avec circuit breaker et une réponse 503 explicite.*

*2. **La communication asynchrone via RabbitMQ** est privilégiée pour tous les événements métier entraînant des effets de bord :*
- *`PaymentCompleted` : émis par Marketplace pour déclencher l'inscription dans Journey.*
- *`ReviewPosted` : émis par Community pour actualiser la note moyenne dans Course.*
- *`CertificateIssued` : émis par Assessment pour que Community attribue un badge.*

*Si le service consommateur tombe en panne, le message reste intact dans la file RabbitMQ et sera traité automatiquement lors de son redémarrage. Cela élimine tout risque de perte de données. »*

---

### 🌟 Slide 9 : Speech Individuel de Selim Hechmi (1 min chrono) - Marketplace & Billing
*« Bonjour Monsieur, je suis **Selim Hechmi**, responsable du microservice **Marketplace & Billing** sur le port 8085.*

*La responsabilité métier de mon microservice est d'assurer l'ensemble du cycle transactionnel d'achat de formations : conversion des sélections en commandes, calcul des montants, exécution du paiement sécurisé en environnement sandbox et édition automatique de la facture numérique.*

*Mon modèle de données comprend 4 entités JPA interconnectées :*
- *`Order` : représentant la commande et son statut.*
- *`OrderItem` : les lignes de commande référençant le `courseId` distant.*
- *`Payment` : enregistrant la transaction, la méthode et la date.*
- *`Invoice` : la facture conforme avec son numéro unique et lien PDF.*

*Mes principaux endpoints REST sont :*
- *`POST /api/orders` (création de la commande, réponse 201 Created).*
- *`POST /api/orders/{id}/payments` (validation du paiement).*
- *`GET /api/invoices/{id}` (consultation de la facture).*

*Mon interaction majeure est asynchrone : dès que le paiement réussit, mon service publie immédiatement l'événement **`PaymentCompleted`** sur RabbitMQ et retourne un code **201** au client. Le paiement n'attend pas la création de l'inscription. Si le service Journey est temporairement indisponible, le message reste sécurisé dans RabbitMQ et sera dépilé à la reprise, avec gestion d'idempotence pour éviter tout doublon.*

*J'ai également contribué avec toute l'équipe au socle commun `identity-service` pour la sécurisation de nos endpoints par jeton JWT. »*

---

## ❓ Les 12 Questions Probables du Professeur & Réponses Types (Pour le 20/20)

| N° | Question du Professeur | Réponse Courte & Précise à Donner |
|---|---|---|
| **Q1** | **Pourquoi avoir choisi une architecture microservices plutôt qu'un monolithe ?** | *« Pour séparer clairement les capacités métier en contextes bornés (Bounded Contexts), permettre des déploiements indépendants, faire évoluer les technologies de chaque service sans impact global et éliminer le point unique de défaillance. »* |
| **Q2** | **Pourquoi ne pas avoir fait un découpage par table ou par écran ?** | *« Parce qu'un écran ou une table ne représentent pas une responsabilité métier. Une table `Order` et une table `Payment` font partie du même domaine Marketplace. Découper par table recréerait un monolithe distribué avec un couplage excessif. »* |
| **Q3** | **Pourquoi interdire formellement une base de données partagée ?** | *« Une base partagée casse l'autonomie des microservices, crée un couplage fort sur le schéma SQL, engendre des conflits de verrous en écriture et empêche de choisir le type de base le plus adapté (ex: NoSQL pour Community et SQL pour Marketplace). »* |
| **Q4** | **Quand utilisez-vous OpenFeign et quand utilisez-vous RabbitMQ ?** | *« OpenFeign est synchrone : nous l'utilisons quand nous avons un besoin impératif d'une réponse immédiate en lecture (ex: vérifier qu'un étudiant est inscrit). RabbitMQ est asynchrone : nous l'utilisons pour les événements métier à diffusion ou à traitement différé sans bloquer l'utilisateur (ex: création d'inscription après paiement). »* |
| **Q5** | **À quoi sert l'API Gateway ? Pourquoi ne pas appeler directement les microservices ?** | *« L'API Gateway est l'unique point d'entrée public (:8080). Elle évite d'exposer les ports internes de chaque microservice, gère le routage dynamique, centralise l'authentification des jetons JWT, gère les CORS et limite les requêtes (Rate Limiting). »* |
| **Q6** | **À quoi sert Netflix Eureka ?** | *« C'est le registre de services (Service Discovery). À leur démarrage, les microservices s'enregistrent dynamiquement auprès d'Eureka. La Gateway et les clients Feign interrogent Eureka pour obtenir les adresses IP et ports réels, évitant ainsi toute configuration d'URL codée en dur. »* |
| **Q7** | **Que se passe-t-il si RabbitMQ ou le service consommateur tombe en panne ?** | *« Le message reste persisté dans la file d'attente durable de RabbitMQ. Dès que le consommateur redémarre, il dépile le message. Nous prévoyons également des Dead-Letter Queues (DLQ) après plusieurs échecs et un contrôle d'idempotence pour ne jamais traiter deux fois la même commande. »* |
| **Q8** | **Que se passe-t-il si un service appelé en OpenFeign ne répond pas ?** | *« L'appel lève une exception après un timeout configuré (ex: 2 secondes). Grâce à un Circuit Breaker (Resilience4j / Fallback), le service appelant retourne une réponse contrôlée avec le code HTTP `503 Service Unavailable` au lieu de bloquer indéfiniment son propre thread. »* |
| **Q9** | **Pourquoi un microservice IA distinct ? N'est-ce pas trop lourd ?** | *« C'est notre microservice de technologie avancée. Son domaine métier (plans d'apprentissage, recommandations et chatbot) possède un rythme de déploiement et des exigences de calcul très différents du catalogue. En Validation 1, nous validons ses contrats d'API et nous nous branchons sur une API de modèle sans entraîner de modèle de zéro. »* |
| **Q10** | **Comment gérez-vous la sécurité et les autorisations ?** | *« Les requêtes s'authentifient auprès d'Identity / Keycloak qui émet un jeton JWT contenant l'identifiant et les rôles (`ROLE_STUDENT`, `ROLE_INSTRUCTOR`, `ROLE_ADMIN`). Chaque microservice décode le JWT de manière autonome sans réinterroger la base à chaque requête. »* |
| **Q11** | **Comment faites-vous une jointure entre deux microservices ?** | *« En microservices, on ne fait jamais de jointure SQL `JOIN` entre bases. On conserve uniquement les clés primaires distantes (`courseId`, `userId`). Si des données composites doivent être affichées, le frontend effectue les requêtes ou la Gateway / un Backend-For-Frontend effectue l'agrégation. »* |
| **Q12** | **Quelle est l'organisation de votre dépôt Git ?** | *« Notre dépôt public respecte strictement l'arborescence demandée : `Backend/` (7 projets Spring Boot initialisés avec Maven), `Frontend/`, `Documentation/Presentation/`, `Documentation/Diagrams/` et `README.md`. Nous utilisons la stratégie GitFlow avec des branches par membre et un tableau GitHub Projects pour le suivi des tâches. »* |

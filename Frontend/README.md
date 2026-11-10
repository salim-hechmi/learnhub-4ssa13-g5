# Frontend LearnHub - Application Cliente

Ce dossier contient l'application cliente Web de la plateforme **LearnHub**.

---

## 🎯 Architecture Frontend & Consommation des APIs
L'application cliente interagit avec l'ensemble des microservices à travers **un point d'entrée unique** : l'**API Gateway** (Spring Cloud Gateway sur le port `8080`).

Le frontend ne communique **jamais directement** avec les ports internes des microservices individuels. Toutes les requêtes sont préfixées par `/api` et routées dynamiquement via Eureka vers les microservices cibles :
- `/api/auth/**` & `/api/users/**` $\rightarrow$ `identity-service`
- `/api/courses/**` $\rightarrow$ `course-service`
- `/api/enrollments/**` & `/api/lessons/**` $\rightarrow$ `learning-journey-service`
- `/api/quizzes/**` & `/api/certificates/**` $\rightarrow$ `assessment-service`
- `/api/orders/**` & `/api/payments/**` $\rightarrow$ `marketplace-service`
- `/api/courses/{id}/reviews` & `/api/threads/**` $\rightarrow$ `community-service`
- `/api/study-plans/**` & `/api/chat/**` $\rightarrow$ `ai-learning-coach-service`

---

## 💻 Modules et Vues de l'Application
1. **Authentification & Profil :** Inscription, connexion, récupération de token JWT et profil utilisateur.
2. **Catalogue de Cours :** Exploration des catégories, filtres, détails d'un cours, chapitres et avis vérifiés.
3. **Espace Mon Apprentissage :** Liste des inscriptions actives, jauges de progression et validation des leçons.
4. **Quiz & Certifications :** Interface de passage d'évaluation, calcul immédiat du score et téléchargement de certificat officiel.
5. **Panier & Facturation (Marketplace) :** Récapitulatif de commande, paiement simulé sécurisé et consultation de factures.
6. **Communauté & Forums :** Publication d'avis vérifiés, fils de discussion par formation et tableau des badges de réussite.
7. **Assistant IA (AI Learning Coach) :** Générateur de planning de révision personnalisé et tuteur conversationnel en temps réel.

---

## 🚀 Lancement Rapide
\`\`\`bash
# Installation des dépendances légères
npm install

# Démarrage de l'interface en mode développement sur le port 3000
npm start
\`\`\`
L'application sera accessible sur : **`http://localhost:3000`**

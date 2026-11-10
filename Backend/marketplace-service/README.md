# marketplace-service

Microservice Marketplace & Billing (Responsable : Selim Hechmi) - Commandes, paiements simulés et factures

## 📌 Informations techniques
- **Port d'écoute :** `8085`
- **Base de données dédiée :** `learnhub_marketplace_db` (H2 en mémoire par défaut, compatible MySQL)
- **Framework :** Spring Boot 3.4.3 / Java 17+
- **Documentation OpenAPI / Swagger UI :** [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)
- **Spécification OpenAPI JSON :** [http://localhost:8085/v3/api-docs](http://localhost:8085/v3/api-docs)

## 🧩 Entités métier du Bounded Context
- **`Order`** : Gestion autonome des données du domaine
- **`OrderItem`** : Gestion autonome des données du domaine
- **`Payment`** : Gestion autonome des données du domaine
- **`Invoice`** : Gestion autonome des données du domaine

## 🚀 Lancement rapide
```bash
# Compilation et exécution
mvn clean spring-boot:run
```

## 📡 Endpoints exposés
| Méthode | URI | Code statut | Description |
|---|---|---|---|
| `POST` | `/api/orders` | `201` | Créer une commande à partir des cours sélectionnés |
| `GET` | `/api/orders/{id}` | `200` | Consulter les détails d une commande |
| `POST` | `/api/orders/{id}/payments` | `201` | Effectuer un paiement simulé et déclencher l événement PaymentCompleted |
| `GET` | `/api/payments/{id}` | `200` | Consulter le reçu d un paiement |
| `GET` | `/api/invoices/{id}` | `200` | Télécharger la facture associée à une commande |

## ?? Contribution Personnelle - Selim Hechmi
- Conception int�grale du Bounded Context Marketplace & Billing
- Entit�s Order, OrderItem, Payment, Invoice
- �mission de l'�v�nement asynchrone RabbitMQ PaymentCompleted
- Impl�mentation du contr�leur REST MarketplaceController avec codes HTTP 201 Created et 200 OK

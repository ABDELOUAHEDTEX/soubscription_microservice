# 🚇 Subscription Service

Microservice pour la gestion des abonnements au service de transport urbain.

## 📋 Description

Ce service gère :
- Les plans d'abonnement
- Les abonnements utilisateurs
- La facturation et les paiements
- Les renouvellements automatiques
- L'expiration des abonnements

## 🚀 Démarrage Rapide

### Prérequis
- Java 21
- Maven 3.8+
- PostgreSQL 17.x
- Base de données `subscription_service` créée

### Configuration

1. **Créer la base de données :**
```sql
CREATE DATABASE subscription_service;
```

2. **Configurer `application.yml` :**
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/subscription_service}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
```

> Variables d'environnement disponibles : `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PAYMENT_GATEWAY_PROVIDER`, `PAYMENT_GATEWAY_API_KEY`, `PAYMENT_GATEWAY_WEBHOOK_SECRET`

3. **Démarrer le service :**
```bash
# Depuis le dossier backend
mvn spring-boot:run -pl subscription-service

# Ou depuis IntelliJ
# Run SubscriptionServiceApplication
```

4. **Accéder à l'API :**
- API REST : http://localhost:8085/api/subscriptions
- Swagger UI : http://localhost:8085/swagger-ui.html
- Actuator Health : http://localhost:8085/actuator/health

## 📚 Endpoints Principaux

### Plans
- `GET /api/subscriptions/plans` - Liste des plans actifs
- `GET /api/subscriptions/plans/{id}` - Détails d'un plan
- `GET /api/subscriptions/plans/code/{code}` - Plan par code

### Abonnements
- `POST /api/subscriptions` - Créer un abonnement
- `GET /api/subscriptions/{id}` - Détails d'un abonnement
- `GET /api/subscriptions/users/{userId}` - Abonnements d'un utilisateur
- `PUT /api/subscriptions/{id}` - Mettre à jour un abonnement
- `POST /api/subscriptions/{id}/cancel` - Annuler un abonnement
- `POST /api/subscriptions/{id}/renew` - Renouveler un abonnement
- `POST /api/subscriptions/{id}/activate` - Activer un abonnement

### Facturation
- `GET /api/subscriptions/billing/subscriptions/{id}` - Historique de facturation
- `GET /api/subscriptions/billing/payments/{id}` - Détails d'un paiement
- `GET /api/subscriptions/billing/subscriptions/{id}/total` - Montant total payé

## 🧪 Tests

```bash
# Tous les tests
mvn test

# Tests unitaires uniquement
mvn test -Dtest=*Test

# Tests d'intégration uniquement
mvn test -Dtest=*IntegrationTest
```

## 📦 Structure du Projet

```
subscription-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/transport/subscription/
│   │   │       ├── controller/      # Controllers REST
│   │   │       ├── service/         # Services métier
│   │   │       ├── repository/      # Repositories JPA
│   │   │       ├── model/           # Entités JPA
│   │   │       ├── dto/             # DTOs (Request/Response)
│   │   │       ├── mapper/          # Mappers MapStruct
│   │   │       ├── exception/       # Exceptions personnalisées
│   │   │       ├── scheduler/       # Tâches planifiées
│   │   │       ├── config/          # Configurations
│   │   │       └── util/            # Utilitaires
│   │   └── resources/
│   │       ├── application.yml      # Configuration
│   │       └── db/migration/       # Migrations Flyway
│   └── test/                        # Tests
└── pom.xml
```

## 🔧 Technologies Utilisées

- **Spring Boot 3.4.4** - Framework
- **Spring Data JPA** - Accès aux données
- **PostgreSQL 17** - Base de données
- **Flyway** - Migrations
- **MapStruct** - Mapping DTO/Entity
- **Lombok** - Réduction du code
- **Swagger/OpenAPI** - Documentation API
- **JUnit 5** - Tests
- **Mockito** - Mocks pour tests

## 📝 Notes

- Les dossiers `event/consumer/` et `event/producer/` sont prêts pour l'intégration Kafka/RabbitMQ (non implémenté pour l'instant)
- La génération de QR code est simplifiée (format texte)
- Les renouvellements automatiques s'exécutent tous les jours à 2h00
- L'expiration des abonnements est vérifiée tous les jours à 3h00

## ✅ Statut

**Le service est FINALISÉ et PRÊT pour utilisation indépendante.**

Voir `REVIEW_REPORT.md` pour le rapport de revue complet.


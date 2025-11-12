# 🎯 Plan d'Action - Amélioration du Subscription Service

## 📊 Score Global : 70% ✅

---

## 🔴 PRIORITÉ 1 - CRITIQUE (À faire en premier)

### 1. PaymentGateway - Intégration Passerelle de Paiement

**Statut** : ⚠️ Partiel (implémentation mock en place)  
**Impact** : Bloquant pour les paiements réels  
**Effort** : Moyen (2-3 jours)

**Actions** :
1. ✅ Créer interface `PaymentGateway` dans `service/PaymentGateway.java`
   - Méthodes : `processPayment()`, `refundPayment()`, `createCustomer()`, `verifyWebhookSignature()`
2. ✅ Créer `PaymentGatewayConfig.java` dans `config/`
   - Configuration Stripe ou PayPal
   - API keys depuis variables d'environnement
3. 🔄 Créer `StripePaymentGateway.java` dans `service/impl/`
   - Implémentation avec Stripe SDK
   - Gestion des erreurs
4. 🔄 Ajouter dépendance spécifique (Stripe/PayPal) dans `pom.xml` :
   ```xml
   <dependency>
       <groupId>com.stripe</groupId>
       <artifactId>stripe-java</artifactId>
   </dependency>
   ```
5. ✅ Créer `ProcessPaymentRequest.java` dans `dto/request/`
6. ✅ Modifier `BillingService` / `PaymentService` pour utiliser `PaymentGateway`

**Où** : `service/PaymentGateway.java`, `config/PaymentGatewayConfig.java`, `service/impl/StripePaymentGateway.java`

---

### 2. Variables d'Environnement pour Secrets

**Statut** : ⚠️ Partiel (placeholders ajoutés, .env à documenter)  
**Impact** : Sécurité critique  
**Effort** : Faible (1 heure)

**Actions** :
1. 🔄 Créer `application-prod.yml` (dans `.gitignore`)
2. ✅ Modifier `application.yml` pour utiliser `${DB_USERNAME}`, `${DB_PASSWORD}`
3. ✅ Documenter les variables dans `README.md`
4. 🔄 Créer `.env.example` avec template

**Où** : `application.yml`, `application-prod.yml`, `.env.example`

**Exemple** :
```yaml
spring:
  datasource:
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD}
```

---

### 3. ProcessPaymentRequest DTO

**Statut** : ✅ Réalisé  
**Impact** : Structure des paiements  
**Effort** : Faible (30 min)

**Actions** :
1. ✅ Créer `dto/request/ProcessPaymentRequest.java`
2. ✅ Ajouter validations : `@NotNull`, `@Positive`, `@NotBlank`
3. ✅ Utiliser dans `PaymentService.processPayment()`

**Où** : `dto/request/ProcessPaymentRequest.java`

---

## 🟡 PRIORITÉ 2 - IMPORTANT (À faire ensuite)

### 4. QRCodeService Complet

**Statut** : ⚠️ Partiel (format texte simple)  
**Impact** : Fonctionnalité QR code  
**Effort** : Moyen (1 jour)

**Actions** :
1. Créer `service/QRCodeService.java` et `QRCodeServiceImpl.java`
2. Ajouter dépendance ZXing dans `pom.xml` :
   ```xml
   <dependency>
       <groupId>com.google.zxing</groupId>
       <artifactId>core</artifactId>
   </dependency>
   <dependency>
       <groupId>com.google.zxing</groupId>
       <artifactId>javase</artifactId>
   </dependency>
   ```
3. Implémenter génération avec signature
4. Implémenter validation
5. Ajouter endpoint `GET /api/subscriptions/{id}/qrcode` qui retourne image

**Où** : `service/QRCodeService.java`, `service/QRCodeServiceImpl.java`

---

### 5. Pause/Resume Subscriptions

**Statut** : ❌ Manquant  
**Impact** : Fonctionnalités complètes  
**Effort** : Faible (2 heures)

**Actions** :
1. Ajouter méthodes dans `SubscriptionService` :
   - `pauseSubscription(UUID id)`
   - `resumeSubscription(UUID id)`
2. Implémenter dans `SubscriptionServiceImpl`
3. Ajouter endpoints dans `SubscriptionController` :
   - `PUT /api/subscriptions/{id}/pause`
   - `PUT /api/subscriptions/{id}/resume`
4. Gérer le statut PAUSED dans le scheduler

**Où** : `service/SubscriptionService.java`, `controller/SubscriptionController.java`

---

### 6. Admin Endpoints pour Plans

**Statut** : ❌ Manquant  
**Impact** : Gestion des plans  
**Effort** : Moyen (1 jour)

**Actions** :
1. Créer DTOs : `CreatePlanRequest`, `UpdatePlanRequest`
2. Ajouter méthodes dans `PlanService` :
   - `createPlan()`, `updatePlan()`, `deactivatePlan()`
3. Implémenter dans `PlanServiceImpl`
4. Ajouter endpoints dans `PlanController` :
   - `POST /api/subscriptions/plans`
   - `PUT /api/subscriptions/plans/{id}`
   - `DELETE /api/subscriptions/plans/{id}`
5. Ajouter sécurité (admin only) - si JWT implémenté

**Où** : `service/PlanService.java`, `controller/PlanController.java`, `dto/request/CreatePlanRequest.java`

---

### 7. Webhooks Payment

**Statut** : ❌ Manquant  
**Impact** : Callbacks passerelles  
**Effort** : Moyen (1 jour)

**Actions** :
1. Ajouter endpoint dans `BillingController` :
   - `POST /api/subscriptions/billing/webhook`
2. Valider signature webhook
3. Traiter les événements (payment.succeeded, payment.failed)
4. Mettre à jour les paiements et abonnements

**Où** : `controller/BillingController.java`

---

## 🟢 PRIORITÉ 3 - OPTIONNEL (Si nécessaire)

### 8. Events Kafka

**Statut** : ❌ Manquant  
**Impact** : Communication asynchrone  
**Effort** : Moyen (2 jours)

**Actions** :
1. Ajouter dépendance `spring-kafka` dans `pom.xml`
2. Créer events dans `event/` :
   - `SubscriptionCreatedEvent`, `SubscriptionRenewedEvent`, etc.
3. Créer `SubscriptionEventProducer` dans `event/producer/`
4. Publier événements dans les services
5. Configurer Kafka dans `application.yml`

**Où** : `event/`, `event/producer/SubscriptionEventProducer.java`

---

### 9. Dockerfile

**Statut** : ❌ Manquant  
**Impact** : Déploiement  
**Effort** : Faible (1 heure)

**Actions** :
1. Créer `Dockerfile` multi-stage
2. Créer `.dockerignore`
3. Optimiser l'image (<200MB)

**Où** : `Dockerfile` à la racine

---

### 10. TestContainers

**Statut** : ❌ Manquant  
**Impact** : Tests plus réalistes  
**Effort** : Faible (2 heures)

**Actions** :
1. Ajouter dépendance `testcontainers` dans `pom.xml`
2. Créer tests avec vraie base PostgreSQL
3. Remplacer H2 par TestContainers

**Où** : `src/test/java/`

---

## 📋 CHECKLIST RAPIDE

### À faire immédiatement (1-2 jours)
- [ ] PaymentGateway + Stripe
- [ ] Variables d'environnement
- [ ] ProcessPaymentRequest

### À faire cette semaine (3-5 jours)
- [ ] QRCodeService complet
- [ ] Pause/Resume subscriptions
- [ ] Admin endpoints plans
- [ ] Webhooks payment

### À faire si nécessaire (optionnel)
- [ ] Events Kafka
- [ ] Dockerfile
- [ ] TestContainers
- [ ] JWT Security
- [ ] Pagination

---

## 🎯 Objectif Final

**Atteindre 90%+ de complétude** pour un service production-ready.

**Actuellement** : 70% ✅  
**Après Priorité 1** : 80% ✅  
**Après Priorité 2** : 90% ✅  
**Après Priorité 3** : 95%+ ✅


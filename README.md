# SouqTech - Plateforme E-Commerce Avancée 🚀

Bienvenue sur **SouqTech**, une version professionnelle et robuste d'une plateforme e-commerce Full-Stack.

## ✨ Fonctionnalités Uniques (Version Pro)

### ⭐ Système d'Avis et Notes (Reviews)
- Les utilisateurs peuvent noter les produits (1-5 étoiles)
- Commentaires détaillés
- Calcul automatique de la moyenne
- Badge "Achat Vérifié" (Backend Ready)
- Vote "Utile" sur les avis

### ❤️ Liste de Souhaits (Wishlist)
- Ajouter/Retirer des produits en un clic
- **Partage Social** : Partagez votre wishlist avec un lien unique
- Mode Privé/Public

### 🛒 Fonctionnalités de base
- Catalogue complet avec pagination
- Recherche avancée
- Panier persistant
- Authentification Sécurisée (JWT)
- Gestion de profil

## 🛠️ Stack Technique

- **Frontend** : Angular 16, Material Design, RxJS
- **Backend** : Spring Boot 3, Spring Security, JPA
- **Database** : MySQL 8
- **Documentation** : OpenAPI 3 (Swagger)

## 🚀 Installation Rapide

1. **Base de données**
   ```sql
   CREATE DATABASE `sb-ecommerce-dev`;
   ```

2. **Lancer le Backend**
   ```bash
   cd src/backend
   mvn spring-boot:run
   ```

3. **Lancer le Frontend**
   ```bash
   cd src/frontend
   ng serve
   ```

## 📚 Documentation API

Une fois lancé, accédez à Swagger UI pour tester en direct : `http://localhost:8080/swagger-ui.html`

### 🔐 Authentification
- `POST /api/auth/register` : Création de compte client
- `POST /api/auth/login` : Connexion et obtention du Token JWT

### 📦 Produits & Catalogue
- `GET /api/products` : Liste des produits (paginée, recherche par nom/catégorie)
- `GET /api/products/{id}` : Détails complets d'un produit
- `GET /api/product-category` : Liste des catégories disponibles

### 🛒 Commandes & Panier
- `POST /api/checkout/purchase` : Valider et payer une commande
- `GET /api/orders` : Historique des commandes de l'utilisateur connecté

### ⭐ Avis & Notes (Reviews)
- `POST /api/reviews` : Ajouter un avis sur un produit
- `GET /api/reviews/product/{productId}` : Consulter les avis d'un produit


### ❤️ Liste de Souhaits (Wishlist)
- `GET /api/wishlist` : Consulter ma wishlist
- `PUT /api/wishlist/toggle/{productId}` : Ajouter ou retirer un produit
- `GET /api/wishlist/shared/{token}` : Accéder à une wishlist publique (via lien de partage)

---

## 🚀 Infrastructure CI/CD

Ce projet utilise une approche double CI/CD pour une fiabilité maximale :

1. **GitHub Actions (Cloud CI)** :
   - Déclenchée automatiquement à chaque push sur `main`.
   - Exécute les builds Maven et les tests dans le cloud.
   - Intégrée avec SonarCloud pour l'analyse de qualité.

2. **Jenkins (Local CI)** :
   - Fonctionne sur un environnement local via un tunnel `ngrok`.
   - **Domaine Statique** : `https://overwary-lien-tremulously.ngrok-free.dev`
   - Gère les tâches de build locales et fournit un tableau de bord détaillé.
   - **Trigger automatique** : Configuré avec Webhook + Poll SCM (1 min).

---
*Projet SouqTech - Infrastructure Validée*

---

* V�rification du pipeline CI/CD : JaCoCo activ� (Test du 11 Janvier)

* [Test Trigger] Validation de la couverture SonarCloud

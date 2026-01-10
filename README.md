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

Une fois lancé, accédez à Swagger UI : `http://localhost:8080/swagger-ui.html`

### Nouveaux Endpoints
- `POST /api/reviews/product/{id}` : Ajouter un avis
- `GET /api/wishlist` : Voir ma wishlist
- `GET /api/wishlist/shared/{token}` : Voir une wishlist partagée

---
*Personnalisé et Amélioré par Antigravity AI*

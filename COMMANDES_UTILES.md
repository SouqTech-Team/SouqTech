# 🚀 Commandes Utiles - Projet SouqTech

Guide de référence rapide pour toutes les commandes nécessaires au projet.

---

## 🔧 Jenkins - Redémarrage après reboot PC

### Démarrer Jenkins
```powershell
docker start jenkins
```

### Configurer les permissions Docker
```powershell
docker exec -u root jenkins chmod 666 /var/run/docker.sock
```

### Vérifier que Jenkins fonctionne
```powershell
# Voir si Jenkins tourne
docker ps -f name=jenkins

# Tester Docker dans Jenkins
docker exec jenkins docker ps
```

### Accéder à Jenkins
```
http://localhost:8080
```

---

## 🐳 Docker & Docker Compose

### Démarrer l'application complète
```powershell
docker-compose up -d
```

### Arrêter l'application
```powershell
docker-compose down
```

### Voir les logs en temps réel
```powershell
# Tous les services
docker-compose logs -f

# Backend seulement
docker-compose logs -f backend

# Frontend seulement
docker-compose logs -f frontend
```

### Mettre à jour les images depuis Docker Hub
```powershell
docker-compose pull
docker-compose up -d
```

### Reconstruire les images localement
```powershell
# Rebuild tout
docker-compose up -d --build

# Rebuild backend seulement
docker-compose up -d --build backend

# Rebuild frontend seulement
docker-compose up -d --build frontend
```

### Voir les conteneurs actifs
```powershell
docker ps
```

### Voir tous les conteneurs (actifs et arrêtés)
```powershell
docker ps -a
```

### Supprimer tous les conteneurs arrêtés
```powershell
docker container prune
```

---

## 🔨 Backend (Spring Boot)

### Se déplacer dans le dossier backend
```powershell
cd src/backend
```

### Compiler et tester
```powershell
# Compilation complète + tests
mvn clean verify

# Compilation sans tests
mvn clean package -DskipTests

# Lancer seulement les tests
mvn test

# Voir la couverture de code
mvn jacoco:report
# Rapport disponible dans: target/site/jacoco/index.html
```

### Lancer le backend en local (sans Docker)
```powershell
mvn spring-boot:run
```
**URL Backend** : `http://localhost:8081`

### Build de l'image Docker backend
```powershell
cd src/backend
docker build -t souqtech-backend .
```

---

## 🎨 Frontend (Angular)

### Se déplacer dans le dossier frontend
```powershell
cd src/frontend
```

### Installer les dépendances
```powershell
npm install
```

### Lancer en mode développement
```powershell
npm start
# ou
ng serve
```
**URL Frontend** : `http://localhost:4200`

### Build de production
```powershell
npm run build -- --configuration production
# Fichiers générés dans: dist/angular-ecommerce/
```

### Lancer les tests
```powershell
npm test
```

### Build de l'image Docker frontend
```powershell
cd src/frontend
docker build -t souqtech-frontend .
```

---

## 📊 SonarCloud

### Analyser le code avec SonarCloud
```powershell
cd src/backend
mvn clean verify sonar:sonar -Dsonar.projectKey=SouqTech-Team_SouqTech -Dsonar.organization=souqtech-team -Dsonar.host.url=https://sonarcloud.io
```

### Voir les résultats
```
https://sonarcloud.io/dashboard?id=SouqTech-Team_SouqTech
```

---

## 🔄 Git - Gestion du code

### Vérifier l'état des fichiers
```powershell
git status
```

### Ajouter tous les fichiers modifiés
```powershell
git add .
```

### Ajouter un fichier spécifique
```powershell
git add nom-du-fichier
```

### Créer un commit
```powershell
git commit -m "Description du changement"
```

### Pousser vers GitHub
```powershell
git push origin main
```

### Récupérer les derniers changements
```powershell
git pull origin main
```

### Voir l'historique des commits
```powershell
git log --oneline
```

### Annuler les modifications locales (non commitées)
```powershell
git restore nom-du-fichier
```

---

## 🌐 URLs d'accès

### Application en local
- **Frontend** : http://localhost (ou http://localhost:4200 en dev)
- **Backend API** : http://localhost:8081
- **Swagger UI** : http://localhost:8081/swagger-ui.html

### Jenkins
- **Dashboard** : http://localhost:8080

### SonarCloud
- **Dashboard** : https://sonarcloud.io/dashboard?id=SouqTech-Team_SouqTech

### GitHub
- **Repository** : https://github.com/SouqTech-Team/SouqTech
- **Actions** : https://github.com/SouqTech-Team/SouqTech/actions

### Docker Hub
- **Backend Image** : https://hub.docker.com/r/seifeddine77/souqtech-backend
- **Frontend Image** : https://hub.docker.com/r/seifeddine77/souqtech-frontend

---

## 🧪 Tests

### Backend - Lancer tous les tests
```powershell
cd src/backend
mvn test
```

### Backend - Lancer un test spécifique
```powershell
mvn test -Dtest=NomDuTest
```

### Backend - Voir le rapport de couverture
```powershell
mvn jacoco:report
# Ouvrir: target/site/jacoco/index.html
```

### Frontend - Lancer les tests
```powershell
cd src/frontend
npm test
```

---

## 🔍 Dépannage

### Jenkins ne démarre pas
```powershell
# Voir les logs
docker logs jenkins

# Redémarrer Jenkins
docker restart jenkins
```

### Erreur de permissions Docker dans Jenkins
```powershell
docker exec -u root jenkins chmod 666 /var/run/docker.sock
```

### Port déjà utilisé
```powershell
# Voir quel processus utilise le port 8080
netstat -ano | findstr :8080

# Tuer le processus (remplacer PID par le numéro du processus)
taskkill /PID <PID> /F
```

### Nettoyer Docker (libérer de l'espace)
```powershell
# Supprimer les images non utilisées
docker image prune -a

# Supprimer les volumes non utilisés
docker volume prune

# Tout nettoyer (ATTENTION: supprime tout ce qui n'est pas utilisé)
docker system prune -a --volumes
```

### Réinitialiser complètement l'application
```powershell
# Arrêter et supprimer tous les conteneurs
docker-compose down

# Supprimer les volumes
docker-compose down -v

# Redémarrer proprement
docker-compose up -d
```

---

## 📦 Build complet du projet

### Build complet (Backend + Frontend + Docker)
```powershell
# 1. Backend
cd src/backend
mvn clean verify
docker build -t seifeddine77/souqtech-backend:latest .

# 2. Frontend
cd ../frontend
npm install
npm run build -- --configuration production
docker build -t seifeddine77/souqtech-frontend:latest .

# 3. Retour à la racine
cd ../..

# 4. Lancer avec Docker Compose
docker-compose up -d
```

---

## 🚀 Workflow de développement typique

### 1. Démarrer la journée
```powershell
# Démarrer Jenkins (si redémarrage PC)
docker start jenkins
docker exec -u root jenkins chmod 666 /var/run/docker.sock

# Récupérer les derniers changements
git pull origin main

# Démarrer l'application
docker-compose up -d
```

### 2. Développer une fonctionnalité
```powershell
# Créer une branche (optionnel)
git checkout -b feature/ma-nouvelle-fonctionnalite

# Modifier le code...

# Tester localement
cd src/backend
mvn test

cd ../frontend
npm test
```

### 3. Pousser les changements
```powershell
# Vérifier les modifications
git status

# Ajouter les fichiers
git add .

# Créer un commit
git commit -m "feat: Description de la fonctionnalité"

# Pousser vers GitHub
git push origin main
```

### 4. Vérifier les pipelines
- **GitHub Actions** : https://github.com/SouqTech-Team/SouqTech/actions
- **Jenkins** : http://localhost:8080

---

## 💡 Astuces

### Alias PowerShell utiles (à ajouter dans votre profil)
```powershell
# Ouvrir le profil PowerShell
notepad $PROFILE

# Ajouter ces alias:
function Start-Jenkins {
    docker start jenkins
    docker exec -u root jenkins chmod 666 /var/run/docker.sock
    Write-Host "✅ Jenkins démarré!" -ForegroundColor Green
}

function Start-App {
    docker-compose up -d
    Write-Host "✅ Application démarrée!" -ForegroundColor Green
}

function Stop-App {
    docker-compose down
    Write-Host "✅ Application arrêtée!" -ForegroundColor Green
}
```

**Utilisation** :
```powershell
Start-Jenkins
Start-App
Stop-App
```

---

**📅 Dernière mise à jour : 20 Janvier 2026**

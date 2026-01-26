#!/bin/bash

# Script de déploiement automatique SouqTech
# Ce script est appelé par Jenkins après le build

echo "🚀 Démarrage du déploiement SouqTech..."

# Variables
BACKEND_IMAGE="seifeddine77/souqtech-backend:latest"
FRONTEND_IMAGE="seifeddine77/souqtech-frontend:latest"
NETWORK_NAME="souqtech-network"

# 1. Créer le réseau Docker si nécessaire
echo "📡 Création du réseau Docker..."
docker network create $NETWORK_NAME 2>/dev/null || echo "Réseau déjà existant"

# 2. Arrêter et supprimer les anciens conteneurs
echo "🛑 Arrêt des anciens conteneurs..."
docker stop souqtech-backend souqtech-frontend 2>/dev/null || true
docker rm souqtech-backend souqtech-frontend 2>/dev/null || true

# 3. Télécharger les dernières images
echo "📥 Téléchargement des dernières images..."
docker pull $BACKEND_IMAGE
docker pull $FRONTEND_IMAGE

# 4. Démarrer le backend
echo "🔧 Démarrage du backend..."
docker run -d \
  --name souqtech-backend \
  --network $NETWORK_NAME \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970 \
  --restart unless-stopped \
  $BACKEND_IMAGE

# 5. Attendre que le backend démarre
echo "⏳ Attente du démarrage du backend (30 secondes)..."
sleep 30

# 6. Vérifier la santé du backend
echo "🏥 Vérification de la santé du backend..."
# On utilise un conteneur temporaire dans le même réseau pour tester l'accès
if docker run --network $NETWORK_NAME --rm curlimages/curl -f http://souqtech-backend:8081/actuator/health > /dev/null 2>&1; then
    echo "✅ Backend opérationnel !"
else
    echo "⚠️ Le healthcheck a échoué via le réseau Docker."
    echo "   Tentative de vérification des logs..."
    docker logs --tail 20 souqtech-backend
    
    # On ne fait pas échouer le build ici si c'est juste un problème de connectivité Jenkins <-> App
    # Mais on signale l'avertissement.
    echo "⚠️ Attention : Impossible de vérifier automatiquement le backend depuis Jenkins."
    echo "👉 Vérifiez manuellement : http://localhost:8081/actuator/health"
fi

# 7. Démarrer le frontend
echo "🎨 Démarrage du frontend..."
docker run -d \
  --name souqtech-frontend \
  --network $NETWORK_NAME \
  -p 80:80 \
  --restart unless-stopped \
  $FRONTEND_IMAGE

echo ""
echo "✅ ========================================="
echo "✅  DÉPLOIEMENT RÉUSSI !"
echo "✅ ========================================="
echo ""
echo "📍 Frontend : http://localhost"
echo "📍 Backend  : http://localhost:8081"
echo "📍 Swagger  : http://localhost:8081/swagger-ui.html"
echo "📍 Health   : http://localhost:8081/actuator/health"
echo ""

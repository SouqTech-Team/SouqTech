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

# 2. Renommer les anciens conteneurs au lieu de les supprimer (pour rollback)
echo "🔄 Sauvegarde des conteneurs actuels..."
docker rename souqtech-backend souqtech-backend-previous 2>/dev/null || true
docker rename souqtech-frontend souqtech-frontend-previous 2>/dev/null || true
docker stop souqtech-backend-previous souqtech-frontend-previous 2>/dev/null || true

# 3. Télécharger les dernières images
echo "📥 Téléchargement des dernières images..."
docker pull $BACKEND_IMAGE
docker pull $FRONTEND_IMAGE

# 4. Démarrer le backend
echo "🔧 Démarrage du backend..."
if ! docker run -d \
  --name souqtech-backend \
  --network $NETWORK_NAME \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970 \
  --restart unless-stopped \
  $BACKEND_IMAGE; then
    echo "❌ Échec du démarrage du backend, rollback..."
    docker stop souqtech-backend 2>/dev/null || true
    docker rm souqtech-backend 2>/dev/null || true
    docker rename souqtech-backend-previous souqtech-backend
    docker start souqtech-backend
    exit 1
fi

# 5. Attendre que le backend démarre avec retry
echo "⏳ Attente du démarrage du backend..."
MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if docker run --network $NETWORK_NAME --rm curlimages/curl -f http://souqtech-backend:8081/actuator/health > /dev/null 2>&1; then
        echo "✅ Backend opérationnel après $((RETRY_COUNT * 2)) secondes !"
        break
    fi
    
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
        echo "❌ Le backend n'a pas démarré après $((MAX_RETRIES * 2)) secondes, rollback..."
        docker logs --tail 50 souqtech-backend
        docker stop souqtech-backend
        docker rm souqtech-backend
        docker rename souqtech-backend-previous souqtech-backend
        docker start souqtech-backend
        exit 1
    fi
    
    sleep 2
done

# Supprimer l'ancien backend si le nouveau fonctionne
docker rm souqtech-backend-previous 2>/dev/null || true

# 6. Démarrer le frontend
echo "🎨 Démarrage du frontend..."
if ! docker run -d \
  --name souqtech-frontend \
  --network $NETWORK_NAME \
  -p 80:80 \
  --restart unless-stopped \
  $FRONTEND_IMAGE; then
    echo "❌ Échec du démarrage du frontend, rollback..."
    docker stop souqtech-frontend 2>/dev/null || true
    docker rm souqtech-frontend 2>/dev/null || true
    docker rename souqtech-frontend-previous souqtech-frontend
    docker start souqtech-frontend
    exit 1
fi

# Supprimer l'ancien frontend si le nouveau fonctionne
docker rm souqtech-frontend-previous 2>/dev/null || true

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

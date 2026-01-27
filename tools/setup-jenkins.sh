#!/bin/bash

# ============================================
# Script d'initialisation Jenkins pour CI/CD
# Configure Docker, kubectl et les permissions
# ============================================

set -e

echo "🚀 Configuration de Jenkins pour CI/CD..."
echo ""

JENKINS_CONTAINER="jenkins"

# Vérifier si le conteneur Jenkins existe
if ! docker ps -a --format '{{.Names}}' | grep -q "^${JENKINS_CONTAINER}$"; then
    echo "❌ Le conteneur Jenkins n'existe pas !"
    echo "   Créez-le d'abord avec: docker run -d --name jenkins ..."
    exit 1
fi

# Démarrer Jenkins si arrêté
if ! docker ps --format '{{.Names}}' | grep -q "^${JENKINS_CONTAINER}$"; then
    echo "📦 Démarrage du conteneur Jenkins..."
    docker start $JENKINS_CONTAINER
    sleep 5
fi

echo "✅ Jenkins est en cours d'exécution"

# 1. Configurer les permissions Docker
echo ""
echo "🔧 Configuration des permissions Docker..."
docker exec -u root $JENKINS_CONTAINER chmod 666 /var/run/docker.sock 2>/dev/null || echo "⚠️ Socket Docker non monté"
echo "✅ Permissions Docker configurées"

# 2. Installer kubectl si nécessaire
echo ""
echo "🔧 Vérification de kubectl..."
if ! docker exec $JENKINS_CONTAINER which kubectl &>/dev/null; then
    echo "📥 Installation de kubectl..."
    docker exec -u root $JENKINS_CONTAINER bash -c "curl -sLO 'https://dl.k8s.io/release/v1.29.0/bin/linux/amd64/kubectl' && chmod +x kubectl && mv kubectl /usr/local/bin/"
    echo "✅ kubectl installé"
else
    echo "✅ kubectl déjà installé"
fi

# 3. Configurer kubeconfig
echo ""
echo "🔧 Configuration de kubeconfig..."
docker exec -u root $JENKINS_CONTAINER mkdir -p /var/jenkins_home/.kube

# Détecter le chemin kubeconfig selon l'OS
if [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]] || [[ -n "$USERPROFILE" ]]; then
    # Windows (Git Bash ou PowerShell)
    KUBE_CONFIG="$USERPROFILE/.kube/config"
else
    # Linux/Mac
    KUBE_CONFIG="$HOME/.kube/config"
fi

if [ -f "$KUBE_CONFIG" ]; then
    docker cp "$KUBE_CONFIG" $JENKINS_CONTAINER:/var/jenkins_home/.kube/config
    docker exec -u root $JENKINS_CONTAINER chown -R jenkins:jenkins /var/jenkins_home/.kube
    docker exec -u root $JENKINS_CONTAINER sed -i 's/127.0.0.1/host.docker.internal/g' /var/jenkins_home/.kube/config
    docker exec -u root $JENKINS_CONTAINER sed -i 's/localhost/host.docker.internal/g' /var/jenkins_home/.kube/config
    echo "✅ kubeconfig configuré"
else
    echo "⚠️ kubeconfig non trouvé à $KUBE_CONFIG"
fi

# 4. Vérifier la connectivité
echo ""
echo "🔍 Vérification de la connectivité..."

echo -n "   Docker: "
if docker exec $JENKINS_CONTAINER docker version --format '{{.Server.Version}}' &>/dev/null; then
    echo "✅ OK ($(docker exec $JENKINS_CONTAINER docker version --format '{{.Server.Version}}'))"
else
    echo "❌ Échec"
fi

echo -n "   Kubernetes: "
if docker exec $JENKINS_CONTAINER kubectl cluster-info &>/dev/null; then
    echo "✅ OK"
else
    echo "❌ Échec"
fi

# Résumé
echo ""
echo "==========================================="
echo "✅  CONFIGURATION JENKINS TERMINÉE !"
echo "==========================================="
echo ""
echo "🔗 Jenkins UI: http://localhost:8080"
echo ""
echo "📋 Pour vérifier manuellement:"
echo "   docker exec jenkins docker version"
echo "   docker exec jenkins kubectl get nodes"
echo ""

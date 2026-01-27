#!/bin/bash

# Script de déploiement automatique SouqTech
# Ce script est appelé par Jenkins après le build
# Version 2.0 - Support Docker natif ET Kubernetes avec Monitoring

echo "🚀 Démarrage du déploiement SouqTech..."

# Variables
BACKEND_IMAGE="seifeddine77/souqtech-backend:latest"
FRONTEND_IMAGE="seifeddine77/souqtech-frontend:latest"
NETWORK_NAME="souqtech-network"
DEPLOY_MODE="${DEPLOY_MODE:-docker}"  # "docker" ou "k8s"

# Détection automatique : si kubectl est disponible et cluster actif, utiliser K8s
if kubectl cluster-info &>/dev/null; then
    echo "✅ Cluster Kubernetes détecté !"
    DEPLOY_MODE="k8s"
else
    echo "ℹ️ Kubernetes non disponible, utilisation de Docker natif"
    DEPLOY_MODE="docker"
fi

echo "📦 Mode de déploiement: $DEPLOY_MODE"
echo ""

# ==============================================
# DÉPLOIEMENT DOCKER NATIF
# ==============================================
deploy_docker() {
    echo "🐳 Déploiement en mode Docker natif..."
    
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

    # 3.5. Démarrer MySQL si nécessaire
    echo "🗄️ Vérification de MySQL (Pipeline)..."
    if ! docker ps --format '{{.Names}}' | grep -q "^mysql$"; then
        echo "Démarrage de MySQL dédié au pipeline..."
        docker rm -f mysql 2>/dev/null || true
        docker run -d \
          --name mysql \
          --network $NETWORK_NAME \
          -e MYSQL_ROOT_PASSWORD="MyS3cur3R00tP@ssw0rd!2026" \
          -e MYSQL_DATABASE=souqtech_db \
          -e MYSQL_USER=souqtech \
          -e MYSQL_PASSWORD="S0uqT3ch$3cur3P@ss2026!" \
          mysql:8.0
        echo "⏳ Attente du démarrage de MySQL (20 secondes)..."
        sleep 20
    else
        echo "✅ MySQL (Pipeline) déjà en cours d'exécution"
    fi

    # 4. Démarrer le backend
    echo "🔧 Démarrage du backend..."
    if ! docker run -d \
      --name souqtech-backend \
      --network $NETWORK_NAME \
      -p 8081:8081 \
      -e SPRING_PROFILES_ACTIVE=prod \
      -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/souqtech_db?allowPublicKeyRetrieval=true&useSSL=false&createDatabaseIfNotExist=true" \
      -e SPRING_DATASOURCE_USERNAME=souqtech \
      -e SPRING_DATASOURCE_PASSWORD="S0uqT3ch\$3cur3P@ss2026!" \
      -e SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQL8Dialect \
      -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
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

    # 5. Attendre que le backend démarre
    echo "⏳ Attente du démarrage du backend (max 150s)..."
    MAX_RETRIES=75
    RETRY_COUNT=0

    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        if docker logs souqtech-backend 2>&1 | grep -q "Started SpringBootEcommerceApplication"; then
            echo "✅ Backend opérationnel après $((RETRY_COUNT * 2)) secondes !"
            break
        fi
        
        echo "⏳ Démarrage en cours... (Tentative $RETRY_COUNT/$MAX_RETRIES)"
        RETRY_COUNT=$((RETRY_COUNT + 1))
        if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
            echo "❌ Le backend n'a pas démarré après $((MAX_RETRIES * 2)) secondes, rollback..."
            docker logs --tail 50 souqtech-backend
            docker stop souqtech-backend 2>/dev/null || true
            docker rm souqtech-backend 2>/dev/null || true
            
            if docker ps -a --format '{{.Names}}' | grep -q "^souqtech-backend-previous$"; then
                echo "🔄 Restauration de la version précédente..."
                docker rename souqtech-backend-previous souqtech-backend
                docker start souqtech-backend
            fi
            exit 1
        fi
        
        sleep 2
    done

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

    docker rm souqtech-frontend-previous 2>/dev/null || true

    echo ""
    echo "✅ ========================================="
    echo "✅  DÉPLOIEMENT DOCKER RÉUSSI !"
    echo "✅ ========================================="
    echo ""
    echo "📍 Frontend : http://localhost"
    echo "📍 Backend  : http://localhost:8081"
    echo "📍 Swagger  : http://localhost:8081/swagger-ui.html"
    echo "📍 Health   : http://localhost:8081/actuator/health"
    echo ""
}

# ==============================================
# DÉPLOIEMENT KUBERNETES
# ==============================================
deploy_kubernetes() {
    echo "☸️  Déploiement en mode Kubernetes..."
    
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    K8S_DIR="$SCRIPT_DIR/k8s"
    MONITORING_DIR="$K8S_DIR/monitoring"
    
    # 1. Créer les namespaces
    echo "📁 Création des namespaces..."
    kubectl apply -f "$K8S_DIR/namespace.yaml"
    kubectl apply -f "$MONITORING_DIR/namespace.yaml"
    
    # 2. Déployer les secrets et configs
    echo "🔐 Déploiement des secrets..."
    kubectl apply -f "$K8S_DIR/mysql-secret.yaml"
    kubectl apply -f "$K8S_DIR/mysql-config.yaml"
    kubectl apply -f "$K8S_DIR/jwt-secret.yaml"
    kubectl apply -f "$MONITORING_DIR/grafana-secret.yaml"
    
    # 3. Déployer les PVCs
    echo "💾 Création des volumes persistants..."
    kubectl apply -f "$K8S_DIR/mysql-pvc.yaml"
    kubectl apply -f "$MONITORING_DIR/prometheus-pvc.yaml"
    kubectl apply -f "$MONITORING_DIR/grafana-pvc.yaml"
    
    # 4. Déployer MySQL
    echo "🗄️ Déploiement de MySQL..."
    kubectl apply -f "$K8S_DIR/mysql-deployment.yaml"
    kubectl apply -f "$K8S_DIR/mysql-service.yaml"
    
    echo "⏳ Attente du démarrage de MySQL..."
    kubectl wait --for=condition=ready pod -l app=mysql -n souqtech --timeout=120s || echo "⚠️ Timeout MySQL"
    
    # 5. Déployer le Backend
    echo "🔧 Déploiement du backend..."
    kubectl apply -f "$K8S_DIR/backend-deployment.yaml"
    kubectl apply -f "$K8S_DIR/backend-service.yaml"
    
    # 6. Déployer le Frontend
    echo "🎨 Déploiement du frontend..."
    kubectl apply -f "$K8S_DIR/frontend-deployment.yaml"
    
    # 7. Déployer le Monitoring (Prometheus + Grafana)
    echo "📊 Déploiement du monitoring..."
    kubectl apply -f "$MONITORING_DIR/prometheus-rbac.yaml"
    kubectl apply -f "$MONITORING_DIR/prometheus-config.yaml"
    kubectl apply -f "$MONITORING_DIR/prometheus-deployment.yaml"
    kubectl apply -f "$MONITORING_DIR/prometheus-service.yaml"
    kubectl apply -f "$MONITORING_DIR/grafana-datasource.yaml"
    kubectl apply -f "$MONITORING_DIR/grafana-deployment.yaml"
    kubectl apply -f "$MONITORING_DIR/grafana-service.yaml"
    
    # 8. Attendre que tous les pods soient prêts
    echo "⏳ Attente du démarrage des services..."
    kubectl wait --for=condition=ready pod -l app=souqtech-backend -n souqtech --timeout=180s || echo "⚠️ Timeout Backend"
    kubectl wait --for=condition=ready pod -l app=frontend -n souqtech --timeout=60s || echo "⚠️ Timeout Frontend"
    kubectl wait --for=condition=ready pod -l app=prometheus -n monitoring --timeout=120s || echo "⚠️ Timeout Prometheus"
    kubectl wait --for=condition=ready pod -l app=grafana -n monitoring --timeout=120s || echo "⚠️ Timeout Grafana"
    
    echo ""
    echo "✅ ========================================="
    echo "✅  DÉPLOIEMENT KUBERNETES RÉUSSI !"
    echo "✅ ========================================="
    echo ""
    echo "📋 État des pods:"
    kubectl get pods -n souqtech
    echo ""
    kubectl get pods -n monitoring
    echo ""
    echo "📍 Accès aux services (NodePort):"
    echo "📍 Frontend   : http://localhost:$(kubectl get svc souqtech-frontend-service -n souqtech -o jsonpath='{.spec.ports[0].nodePort}' 2>/dev/null || echo '80')"
    echo "📍 Backend    : http://localhost:30080"
    echo "📍 Prometheus : http://localhost:30090"
    echo "📍 Grafana    : http://localhost:30300 (admin / P@ssw0rd!2026\$SouqTech#Secure)"
    echo ""
    echo "� Pour voir les métriques dans Grafana:"
    echo "   1. Accédez à http://localhost:30300"
    echo "   2. La datasource Prometheus est déjà configurée"
    echo "   3. Importez un dashboard (ID: 6417, 11074, ou 12900)"
    echo ""
}

# ==============================================
# EXÉCUTION PRINCIPALE
# ==============================================
case "$DEPLOY_MODE" in
    "k8s"|"kubernetes")
        deploy_kubernetes
        ;;
    "docker"|*)
        deploy_docker
        ;;
esac

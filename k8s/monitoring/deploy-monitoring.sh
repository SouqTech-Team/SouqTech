#!/bin/bash

# ============================================
# Script de Déploiement Monitoring SouqTech
# Prometheus + Grafana sur Kubernetes
# ============================================

set -e  # Arrêter en cas d'erreur

echo "🚀 Déploiement de la stack de monitoring SouqTech..."
echo ""

# Couleurs pour les messages
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages
log_info() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warn() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Vérifier que kubectl est installé
if ! command -v kubectl &> /dev/null; then
    log_error "kubectl n'est pas installé. Veuillez l'installer d'abord."
    exit 1
fi

log_info "kubectl est installé"

# ============================================
# 1. Créer le namespace monitoring
# ============================================
echo ""
echo "📁 Étape 1/7 : Création du namespace monitoring..."
kubectl apply -f k8s/monitoring/namespace.yaml
log_info "Namespace 'monitoring' créé"

# ============================================
# 2. Créer les secrets
# ============================================
echo ""
echo "🔐 Étape 2/7 : Création des secrets..."

# Vérifier si le secret existe déjà
if kubectl get secret grafana-secret -n monitoring &> /dev/null; then
    log_warn "Le secret grafana-secret existe déjà. Suppression..."
    kubectl delete secret grafana-secret -n monitoring
fi

kubectl apply -f k8s/monitoring/grafana-secret.yaml
log_info "Secret Grafana créé"

# ============================================
# 3. Créer les RBAC (permissions)
# ============================================
echo ""
echo "🔑 Étape 3/7 : Configuration des permissions RBAC..."
kubectl apply -f k8s/monitoring/prometheus-rbac.yaml
log_info "ServiceAccount et RBAC Prometheus créés"

# ============================================
# 4. Créer les PersistentVolumeClaims
# ============================================
echo ""
echo "💾 Étape 4/7 : Création des volumes persistants..."
kubectl apply -f k8s/monitoring/prometheus-pvc.yaml
kubectl apply -f k8s/monitoring/grafana-pvc.yaml
log_info "PVC créés"

# Attendre que les PVC soient bound
echo "⏳ Attente que les PVC soient liés..."
kubectl wait --for=jsonpath='{.status.phase}'=Bound pvc/prometheus-pvc -n monitoring --timeout=60s || log_warn "Timeout PVC Prometheus (peut être normal)"
kubectl wait --for=jsonpath='{.status.phase}'=Bound pvc/grafana-pvc -n monitoring --timeout=60s || log_warn "Timeout PVC Grafana (peut être normal)"

# ============================================
# 5. Déployer Prometheus
# ============================================
echo ""
echo "📊 Étape 5/7 : Déploiement de Prometheus..."
kubectl apply -f k8s/monitoring/prometheus-config.yaml
kubectl apply -f k8s/monitoring/prometheus-deployment.yaml
kubectl apply -f k8s/monitoring/prometheus-service.yaml
log_info "Prometheus déployé"

# ============================================
# 6. Déployer Grafana
# ============================================
echo ""
echo "📈 Étape 6/7 : Déploiement de Grafana..."
kubectl apply -f k8s/monitoring/grafana-datasource.yaml
kubectl apply -f k8s/monitoring/grafana-deployment.yaml
kubectl apply -f k8s/monitoring/grafana-service.yaml
log_info "Grafana déployé"

# ============================================
# 7. Vérifier le déploiement
# ============================================
echo ""
echo "🔍 Étape 7/7 : Vérification du déploiement..."
echo ""

# Attendre que les pods soient prêts
echo "⏳ Attente que Prometheus soit prêt..."
kubectl wait --for=condition=ready pod -l app=prometheus -n monitoring --timeout=120s || log_warn "Timeout Prometheus"

echo "⏳ Attente que Grafana soit prêt..."
kubectl wait --for=condition=ready pod -l app=grafana -n monitoring --timeout=120s || log_warn "Timeout Grafana"

# Afficher l'état
echo ""
echo "📋 État des ressources dans le namespace monitoring:"
kubectl get all -n monitoring

echo ""
echo "📋 État des PVC:"
kubectl get pvc -n monitoring

echo ""
echo "📋 État des secrets:"
kubectl get secrets -n monitoring

# ============================================
# Informations d'accès
# ============================================
echo ""
echo "=========================================="
echo "✅  DÉPLOIEMENT RÉUSSI !"
echo "=========================================="
echo ""
echo "🔗 Accès aux services:"
echo ""
echo "  📊 Prometheus:"
echo "     URL: http://localhost:30090"
echo "     Targets: http://localhost:30090/targets"
echo "     Config: http://localhost:30090/config"
echo ""
echo "  📈 Grafana:"
echo "     URL: http://localhost:30300"
echo "     Username: admin"
echo "     Password: (voir le secret grafana-secret)"
echo ""
echo "🔐 Pour récupérer le mot de passe Grafana:"
echo "     kubectl get secret grafana-secret -n monitoring -o jsonpath='{.data.admin-password}' | base64 -d"
echo ""
echo "📊 Commandes utiles:"
echo "     kubectl get pods -n monitoring"
echo "     kubectl logs -f deployment/prometheus -n monitoring"
echo "     kubectl logs -f deployment/grafana -n monitoring"
echo "     kubectl port-forward -n monitoring svc/prometheus-service 9090:9090"
echo "     kubectl port-forward -n monitoring svc/grafana-service 3000:3000"
echo ""
echo "🎯 Prochaines étapes:"
echo "  1. Accédez à Grafana (http://localhost:30300)"
echo "  2. La datasource Prometheus est déjà configurée"
echo "  3. Importez des dashboards (ID recommandés: 6417, 11074, 12900)"
echo "  4. Vérifiez que les métriques Spring Boot sont collectées"
echo ""

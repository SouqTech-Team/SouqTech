# ============================================
# Script d'initialisation Jenkins pour CI/CD
# Configure Docker, kubectl et les permissions
# Version PowerShell pour Windows
# ============================================

Write-Host ""
Write-Host "🚀 Configuration de Jenkins pour CI/CD..." -ForegroundColor Cyan
Write-Host ""

$JENKINS_CONTAINER = "jenkins"

# Vérifier si le conteneur Jenkins existe
$containerExists = docker ps -a --format '{{.Names}}' | Select-String -Pattern "^$JENKINS_CONTAINER$"
if (-not $containerExists) {
    Write-Host "❌ Le conteneur Jenkins n'existe pas !" -ForegroundColor Red
    Write-Host "   Créez-le d'abord avec: docker run -d --name jenkins ..."
    exit 1
}

# Démarrer Jenkins si arrêté
$containerRunning = docker ps --format '{{.Names}}' | Select-String -Pattern "^$JENKINS_CONTAINER$"
if (-not $containerRunning) {
    Write-Host "📦 Démarrage du conteneur Jenkins..." -ForegroundColor Yellow
    docker start $JENKINS_CONTAINER
    Start-Sleep -Seconds 5
}

Write-Host "✅ Jenkins est en cours d'exécution" -ForegroundColor Green

# 1. Configurer les permissions Docker
Write-Host ""
Write-Host "🔧 Configuration des permissions Docker..." -ForegroundColor Yellow
docker exec -u root $JENKINS_CONTAINER chmod 666 /var/run/docker.sock 2>$null
Write-Host "✅ Permissions Docker configurées" -ForegroundColor Green

# 2. Installer kubectl si nécessaire
Write-Host ""
Write-Host "🔧 Vérification de kubectl..." -ForegroundColor Yellow
$kubectlExists = docker exec $JENKINS_CONTAINER which kubectl 2>$null
if (-not $kubectlExists) {
    Write-Host "📥 Installation de kubectl..." -ForegroundColor Yellow
    docker exec -u root $JENKINS_CONTAINER bash -c "curl -sLO 'https://dl.k8s.io/release/v1.29.0/bin/linux/amd64/kubectl' && chmod +x kubectl && mv kubectl /usr/local/bin/"
    Write-Host "✅ kubectl installé" -ForegroundColor Green
} else {
    Write-Host "✅ kubectl déjà installé" -ForegroundColor Green
}

# 3. Configurer kubeconfig
Write-Host ""
Write-Host "🔧 Configuration de kubeconfig..." -ForegroundColor Yellow
docker exec -u root $JENKINS_CONTAINER mkdir -p /var/jenkins_home/.kube

$KUBE_CONFIG = "$env:USERPROFILE\.kube\config"
if (Test-Path $KUBE_CONFIG) {
    docker cp $KUBE_CONFIG "${JENKINS_CONTAINER}:/var/jenkins_home/.kube/config"
    docker exec -u root $JENKINS_CONTAINER chown -R jenkins:jenkins /var/jenkins_home/.kube
    docker exec -u root $JENKINS_CONTAINER sed -i 's/127.0.0.1/host.docker.internal/g' /var/jenkins_home/.kube/config
    docker exec -u root $JENKINS_CONTAINER sed -i 's/localhost/host.docker.internal/g' /var/jenkins_home/.kube/config
    Write-Host "✅ kubeconfig configuré" -ForegroundColor Green
} else {
    Write-Host "⚠️ kubeconfig non trouvé à $KUBE_CONFIG" -ForegroundColor Yellow
}

# 4. Vérifier la connectivité
Write-Host ""
Write-Host "🔍 Vérification de la connectivité..." -ForegroundColor Yellow

Write-Host -NoNewline "   Docker: "
$dockerVersion = docker exec $JENKINS_CONTAINER docker version --format '{{.Server.Version}}' 2>$null
if ($dockerVersion) {
    Write-Host "✅ OK ($dockerVersion)" -ForegroundColor Green
} else {
    Write-Host "❌ Échec" -ForegroundColor Red
}

Write-Host -NoNewline "   Kubernetes: "
$k8sCheck = docker exec $JENKINS_CONTAINER kubectl cluster-info 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ OK" -ForegroundColor Green
} else {
    Write-Host "❌ Échec" -ForegroundColor Red
}

# Résumé
Write-Host ""
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "✅  CONFIGURATION JENKINS TERMINÉE !" -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "🔗 Jenkins UI: http://localhost:8080"
Write-Host ""
Write-Host "📋 Pour vérifier manuellement:"
Write-Host "   docker exec jenkins docker version"
Write-Host "   docker exec jenkins kubectl get nodes"
Write-Host ""

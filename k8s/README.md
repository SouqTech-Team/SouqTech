# 🚀 Déploiement Kubernetes - SouqTech

Ce dossier contient les manifests Kubernetes pour déployer SouqTech sur un cluster Kubernetes.

## 📋 Fichiers

- `namespace.yaml` - Namespace dédié pour isoler l'application
- `secrets.yaml` - Secrets (JWT, credentials)
- `backend-deployment.yaml` - Déploiement du backend (3 replicas)
- `frontend-deployment.yaml` - Déploiement du frontend (2 replicas)

## 🎯 Architecture Kubernetes

```
┌─────────────────────────────────────────┐
│         Kubernetes Cluster              │
│                                         │
│  ┌───────────────────────────────────┐ │
│  │     Namespace: souqtech           │ │
│  │                                   │ │
│  │  ┌──────────────────────────┐    │ │
│  │  │  Frontend (2 replicas)   │    │ │
│  │  │  - LoadBalancer Service  │    │ │
│  │  │  - Port 80               │    │ │
│  │  └──────────────────────────┘    │ │
│  │              ↓                    │ │
│  │  ┌──────────────────────────┐    │ │
│  │  │  Backend (3 replicas)    │    │ │
│  │  │  - ClusterIP Service     │    │ │
│  │  │  - Port 8081             │    │ │
│  │  └──────────────────────────┘    │ │
│  │                                   │ │
│  └───────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

## 🚀 Déploiement

### Prérequis
- Cluster Kubernetes (Minikube, Kind, ou cloud provider)
- kubectl configuré

### Commandes de déploiement

```bash
# 1. Créer le namespace
kubectl apply -f namespace.yaml

# 2. Créer les secrets
kubectl apply -f secrets.yaml

# 3. Déployer le backend
kubectl apply -f backend-deployment.yaml

# 4. Déployer le frontend
kubectl apply -f frontend-deployment.yaml

# 5. Vérifier le déploiement
kubectl get all -n souqtech
```

### Vérification

```bash
# Voir les pods
kubectl get pods -n souqtech

# Voir les services
kubectl get services -n souqtech

# Voir les logs du backend
kubectl logs -f deployment/souqtech-backend -n souqtech

# Voir les logs du frontend
kubectl logs -f deployment/souqtech-frontend -n souqtech
```

### Accéder à l'application

```bash
# Obtenir l'IP externe du frontend (LoadBalancer)
kubectl get service souqtech-frontend-service -n souqtech

# Ou utiliser port-forward pour tester localement
kubectl port-forward service/souqtech-frontend-service 8080:80 -n souqtech
# Accéder à: http://localhost:8080
```

## 🔄 Mise à jour

```bash
# Mettre à jour l'image backend
kubectl set image deployment/souqtech-backend backend=seifeddine77/souqtech-backend:v2 -n souqtech

# Mettre à jour l'image frontend
kubectl set image deployment/souqtech-frontend frontend=seifeddine77/souqtech-frontend:v2 -n souqtech

# Voir le statut du rollout
kubectl rollout status deployment/souqtech-backend -n souqtech
```

## 🔙 Rollback

```bash
# Revenir à la version précédente
kubectl rollout undo deployment/souqtech-backend -n souqtech
kubectl rollout undo deployment/souqtech-frontend -n souqtech
```

## 🧹 Nettoyage

```bash
# Supprimer toutes les ressources
kubectl delete namespace souqtech
```

## 📊 Monitoring

```bash
# Voir l'utilisation des ressources
kubectl top pods -n souqtech

# Voir les événements
kubectl get events -n souqtech --sort-by='.lastTimestamp'
```

## 🔐 Sécurité

- Les secrets sont stockés dans Kubernetes Secrets
- Les ressources sont limitées (CPU/Memory)
- Health checks configurés (liveness + readiness)
- Namespace dédié pour l'isolation

## 📈 Scalabilité

```bash
# Scaler le backend
kubectl scale deployment souqtech-backend --replicas=5 -n souqtech

# Scaler le frontend
kubectl scale deployment souqtech-frontend --replicas=3 -n souqtech

# Auto-scaling (HPA)
kubectl autoscale deployment souqtech-backend --cpu-percent=70 --min=3 --max=10 -n souqtech
```

## 🎓 Avantages vs Docker Compose

| Fonctionnalité | Docker Compose | Kubernetes |
|----------------|----------------|------------|
| **Scalabilité** | Manuelle | Automatique (HPA) |
| **Haute disponibilité** | ❌ | ✅ (3 replicas backend) |
| **Load balancing** | Basique | Avancé |
| **Rolling updates** | ❌ | ✅ |
| **Auto-healing** | Restart policy | Self-healing pods |
| **Multi-serveurs** | ❌ | ✅ |

## 💡 Notes

Ce déploiement Kubernetes est préparé pour une **évolution future** du projet.
Actuellement, le projet utilise **Docker Compose** pour sa simplicité, mais ces manifests
démontrent la capacité à migrer vers une architecture cloud-native si nécessaire.

---

**Dernière mise à jour : 25 Janvier 2026**

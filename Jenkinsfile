pipeline {
    agent any

    triggers {
        githubPush()
        pollSCM('* * * * *') // Vérifier les changements toutes les minutes (nécessaire sur localhost)
    }

    tools {
        maven 'Maven 3' 
        jdk 'Java 17'
        nodejs 'node'
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token')
        DOCKER_CREDS = credentials('dockerhub-credentials')
        DOCKER_IMAGE = "seifeddine77/souqtech-backend" 
    }

    stages {
        stage('Checkout') {
            steps {
                echo '[INFO] Recuperation du code source depuis GitHub...'
                checkout scm
            }
        }

        stage('Build & Test Backend') {
            steps {
                echo '[INFO] Demarrage de la compilation et des tests Backend (Spring Boot)...'
                dir('src/backend') {
                    script {
                        if (isUnix()) {
                            sh 'mvn clean verify'
                        } else {
                            bat 'mvn clean verify'
                        }
                    }
                }
            }
        }

        stage('Build Frontend') {
            steps {
                echo '[INFO] Demarrage de la compilation Frontend (Angular)...'
                dir('src/frontend') {
                    script {
                        if (isUnix()) {
                            sh 'npm install && npm run build -- --configuration production'
                        } else {
                            bat 'npm install'
                            bat 'npm run build -- --configuration production'
                        }
                    }
                }
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                echo '[INFO] Analyse de la qualite du code avec SonarCloud...'
                dir('src/backend') {
                    script {
                         def sonarCommand = 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.organization=souqtech-team -Dsonar.projectKey=SouqTech-Team_SouqTech -Dsonar.host.url=https://sonarcloud.io'
                         if (isUnix()) {
                            sh sonarCommand
                        } else {
                            bat sonarCommand
                        }
                    }
                }
            }
        }

        stage('Docker Build & Push Backend') {
            steps {
                echo '[INFO] Construction et Push de l\'image Docker Backend...'
                dir('src/backend') {
                    script {
                        if (isUnix()) {
                            sh "echo '${DOCKER_CREDS_PSW}' | docker login -u ${DOCKER_CREDS_USR} --password-stdin"
                            sh "docker build --no-cache -t ${DOCKER_IMAGE}:latest -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                            sh "docker push ${DOCKER_IMAGE}:latest"
                            sh "docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}"
                        } else {
                            bat "echo %DOCKER_CREDS_PSW% | docker login -u %DOCKER_CREDS_USR% --password-stdin"
                            bat "docker build --no-cache -t %DOCKER_IMAGE%:latest -t %DOCKER_IMAGE%:%BUILD_NUMBER% ."
                            bat "docker push %DOCKER_IMAGE%:latest"
                            bat "docker push %DOCKER_IMAGE%:%BUILD_NUMBER%"
                        }
                    }
                }
            }
        }

        stage('Docker Build & Push Frontend') {
            steps {
                echo '[INFO] Construction et Push de l\'image Docker Frontend...'
                dir('src/frontend') {
                    script {
                        def frontendImage = "seifeddine77/souqtech-frontend"
                        if (isUnix()) {
                            sh "echo '${DOCKER_CREDS_PSW}' | docker login -u ${DOCKER_CREDS_USR} --password-stdin"
                            sh "docker build -t ${frontendImage}:latest -t ${frontendImage}:${BUILD_NUMBER} ."
                            sh "docker push ${frontendImage}:latest"
                            sh "docker push ${frontendImage}:${BUILD_NUMBER}"
                        } else {
                            bat "echo %DOCKER_CREDS_PSW% | docker login -u %DOCKER_CREDS_USR% --password-stdin"
                            bat "docker build -t ${frontendImage}:latest -t ${frontendImage}:%BUILD_NUMBER% ."
                            bat "docker push ${frontendImage}:latest"
                            bat "docker push ${frontendImage}:%BUILD_NUMBER%"
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                echo '[INFO] 🚀 Déploiement sur Kubernetes avec Monitoring...'
                script {
                    if (isUnix()) {
                        // Linux/Mac : utiliser le script bash
                        sh "sed -i 's/\\r\$//' deploy.sh"
                        sh 'chmod +x deploy.sh'
                        sh './deploy.sh'
                    } else {
                        // Windows : utiliser kubectl directement
                        echo '[INFO] Déploiement des namespaces...'
                        bat 'kubectl apply -f k8s/namespace.yaml'
                        bat 'kubectl apply -f k8s/monitoring/namespace.yaml'
                        
                        echo '[INFO] Déploiement des secrets et configs...'
                        bat 'kubectl apply -f k8s/mysql-secret.yaml'
                        bat 'kubectl apply -f k8s/mysql-config.yaml'
                        bat 'kubectl apply -f k8s/jwt-secret.yaml'
                        bat 'kubectl apply -f k8s/monitoring/grafana-secret.yaml'
                        
                        echo '[INFO] Déploiement des volumes persistants...'
                        bat 'kubectl apply -f k8s/mysql-pvc.yaml'
                        bat 'kubectl apply -f k8s/monitoring/prometheus-pvc.yaml'
                        bat 'kubectl apply -f k8s/monitoring/grafana-pvc.yaml'
                        
                        echo '[INFO] Déploiement de MySQL...'
                        bat 'kubectl apply -f k8s/mysql-deployment.yaml'
                        bat 'kubectl apply -f k8s/mysql-service.yaml'
                        
                        echo '[INFO] Déploiement du Backend et Frontend...'
                        bat 'kubectl apply -f k8s/backend-deployment.yaml'
                        bat 'kubectl apply -f k8s/backend-service.yaml'
                        bat 'kubectl apply -f k8s/frontend-deployment.yaml'
                        
                        echo '[INFO] Déploiement du Monitoring (Prometheus + Grafana)...'
                        bat 'kubectl apply -f k8s/monitoring/prometheus-rbac.yaml'
                        bat 'kubectl apply -f k8s/monitoring/prometheus-config.yaml'
                        bat 'kubectl apply -f k8s/monitoring/prometheus-deployment.yaml'
                        bat 'kubectl apply -f k8s/monitoring/prometheus-service.yaml'
                        bat 'kubectl apply -f k8s/monitoring/grafana-datasource.yaml'
                        bat 'kubectl apply -f k8s/monitoring/grafana-deployment.yaml'
                        bat 'kubectl apply -f k8s/monitoring/grafana-service.yaml'
                        
                        echo '[INFO] Redémarrage des déploiements pour utiliser les nouvelles images...'
                        bat 'kubectl rollout restart deployment/souqtech-backend -n souqtech'
                        bat 'kubectl rollout restart deployment/souqtech-frontend -n souqtech'
                        
                        echo '[INFO] Vérification du statut des pods...'
                        bat 'kubectl get pods -n souqtech'
                        bat 'kubectl get pods -n monitoring'
                        
                        echo '[SUCCESS] ✅ Déploiement Kubernetes terminé !'
                        echo '[INFO] Frontend: http://localhost'
                        echo '[INFO] Backend: http://localhost:30080'
                        echo '[INFO] Prometheus: http://localhost:30090'
                        echo '[INFO] Grafana: http://localhost:30300'
                    }
                }
            }
        }
    }

    post {
        always {
            // Sauvegarde des rapports de tests JUnit pour affichage dans Jenkins
            junit 'src/backend/target/surefire-reports/*.xml'
            
            // Enregistrement des rapports JaCoCo pour les graphiques de couverture dans Jenkins
            jacoco execPattern: 'src/backend/target/*.exec', 
                   classPattern: 'src/backend/target/classes', 
                   sourcePattern: 'src/backend/src/main/java', 
                   exclusionPattern: '**/dto/**,**/entity/**,**/error/**,**/config/**'
            
            // Nettoyage des anciennes images Docker
            script {
                if (isUnix()) {
                    sh 'docker image prune -f --filter "until=24h"'
                } else {
                    bat 'docker image prune -f --filter "until=24h"'
                }
            }
        }
        success {
            echo "[SUCCESS] BUILD REUSSI ! La version ${BUILD_NUMBER} est deployee sur Docker Hub."
        }
        failure {
            echo "[FAILURE] BUILD ECHOUE... Veuillez verifier les logs pour corriger l'erreur."
        }
    }
}

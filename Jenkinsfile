pipeline {
    agent any

    triggers {
        githubPush()
        pollSCM('* * * * *') // Check for changes every minute
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
                            sh "docker login -u ${DOCKER_CREDS_USR} -p ${DOCKER_CREDS_PSW}"
                            sh "docker build -t ${DOCKER_IMAGE}:latest -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                            sh "docker push ${DOCKER_IMAGE}:latest"
                            sh "docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}"
                        } else {
                            bat "docker login -u %DOCKER_CREDS_USR% -p %DOCKER_CREDS_PSW%"
                            bat "docker build -t %DOCKER_IMAGE%:latest -t %DOCKER_IMAGE%:%BUILD_NUMBER% ."
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
                            sh "docker login -u ${DOCKER_CREDS_USR} -p ${DOCKER_CREDS_PSW}"
                            sh "docker build -t ${frontendImage}:latest -t ${frontendImage}:${BUILD_NUMBER} ."
                            sh "docker push ${frontendImage}:latest"
                            sh "docker push ${frontendImage}:${BUILD_NUMBER}"
                        } else {
                            bat "docker login -u %DOCKER_CREDS_USR% -p %DOCKER_CREDS_PSW%"
                            bat "docker build -t ${frontendImage}:latest -t ${frontendImage}:%BUILD_NUMBER% ."
                            bat "docker push ${frontendImage}:latest"
                            bat "docker push ${frontendImage}:%BUILD_NUMBER%"
                        }
                    }
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                echo '[INFO] Déploiement automatique de l\'application...'
                script {
                    try {
                        def backendImage = "seifeddine77/souqtech-backend:latest"
                        def frontendImage = "seifeddine77/souqtech-frontend:latest"
                        def netName = "souqtech-net"
                        def jwtSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"

                        if (isUnix()) {
                            // 1. Créer le réseau si nécessaire
                            sh "docker network create ${netName} || true"

                            // 2. Déployer le Backend
                            echo "[INFO] Mise à jour du Backend..."
                            sh "docker pull ${backendImage}"
                            sh "docker stop souqtech-backend || true"
                            sh "docker rm souqtech-backend || true"
                            sh "docker run -d --name souqtech-backend --network ${netName} -p 8081:8081 -e SPRING_PROFILES_ACTIVE=prod -e JWT_SECRET=${jwtSecret} --restart unless-stopped ${backendImage}"

                            // 3. Attendre le démarrage
                            echo "[INFO] Attente du démarrage du backend (45 secondes)..."
                            sh "sleep 45"

                            // 4. Déployer le Frontend
                            echo "[INFO] Mise à jour du Frontend..."
                            sh "docker pull ${frontendImage}"
                            sh "docker stop souqtech-frontend || true"
                            sh "docker rm souqtech-frontend || true"
                            sh "docker run -d --name souqtech-frontend --network ${netName} -p 80:80 --restart unless-stopped ${frontendImage}"

                            // 5. Vérification
                            echo "[INFO] Vérification de l'accès santé..."
                            sh "curl -f http://localhost:8081/actuator/health || exit 1"

                            echo '[SUCCESS] Déploiement réussi ! Application accessible sur http://localhost'
                        } else {
                            // Fallback Windows (peu probable)
                            bat "docker network create ${netName} || exit 0"
                            bat "docker stop souqtech-backend || exit 0"
                        }
                    } catch (Exception e) {
                        echo "[ERROR] Le déploiement a échoué : ${e.message}"
                        error("Déploiement échoué.")
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
        }
        success {
            echo "[SUCCESS] BUILD REUSSI ! La version ${BUILD_NUMBER} est deployee sur Docker Hub."
        }
        failure {
            echo "[FAILURE] BUILD ECHOUE... Veuillez verifier les logs pour corriger l'erreur."
        }
    }
}

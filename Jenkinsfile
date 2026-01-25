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
                        if (isUnix()) {
                            // Arrêter les anciens conteneurs
                            sh 'docker compose down || true'
                            
                            // Récupérer les nouvelles images depuis Docker Hub
                            sh 'docker compose pull'
                            
                            // Démarrer les nouveaux conteneurs
                            sh 'docker compose up -d'
                            
                            // Attendre que les services démarrent
                            echo '[INFO] Attente du démarrage des services (60 secondes)...'
                            sh 'sleep 60'
                            
                            // Vérifier que le backend est accessible
                            echo '[INFO] Vérification du backend...'
                            sh 'curl -f http://localhost:8081/actuator/health || exit 1'
                            
                            // Vérifier que le frontend est accessible
                            echo '[INFO] Vérification du frontend...'
                            sh 'curl -f http://localhost:80 || exit 1'
                            
                            echo '[SUCCESS] Déploiement réussi ! Application accessible.'
                        } else {
                            // Windows
                            bat 'docker compose down || exit 0'
                            bat 'docker compose pull'
                            bat 'docker compose up -d'
                            
                            echo '[INFO] Attente du démarrage des services (60 secondes)...'
                            bat 'timeout /t 60 /nobreak'
                            
                            echo '[INFO] Vérification du backend...'
                            bat 'curl -f http://localhost:8081/actuator/health || exit 1'
                            
                            echo '[INFO] Vérification du frontend...'
                            bat 'curl -f http://localhost:80 || exit 1'
                            
                            echo '[SUCCESS] Déploiement réussi ! Application accessible.'
                        }
                    } catch (Exception e) {
                        echo '[ERROR] Le déploiement a échoué !'
                        echo '[INFO] Tentative de rollback...'
                        
                        if (isUnix()) {
                            sh 'docker compose down'
                            sh 'docker compose up -d'
                        } else {
                            bat 'docker compose down'
                            bat 'docker compose up -d'
                        }
                        
                        error("Déploiement échoué. Rollback effectué.")
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

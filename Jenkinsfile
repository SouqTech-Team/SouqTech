pipeline {
    agent any

    triggers {
        githubPush()
        pollSCM('* * * * *') // Check for changes every minute
    }

    tools {
        // Assurez-vous d'avoir configuré Maven et JDK 17 dans "Global Tool Configuration" de Jenkins
        // Adaptez les noms si nécessaire (ex: "Maven 3.9", "JDK 17")
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
                checkout scm
            }
        }

        stage('Build & Test Backend') {
            steps {
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
            echo 'Build Backend réussi !'
        }
        failure {
            echo 'Build Backend échoué.'
        }
    }
}

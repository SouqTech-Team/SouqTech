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
    }

    environment {
        // Pour activer SonarCloud sur Jenkins :
        // 1. Ajoutez votre token Sonar comme "Secret text" dans les identifiants Jenkins (ID: sonar-token)
        // 2. Décommentez la section environment et le stage 'SonarCloud Analysis' ci-dessous
        
        // SONAR_TOKEN = credentials('sonar-token') 
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

        /* 
        // --- Stage SonarCloud (Optionnel) ---
        // Nécessite le plugin SonarQube Scanner sur Jenkins
        stage('SonarCloud Analysis') {
            steps {
                dir('src/backend') {
                    script {
                         def sonarCommand = 'mvn sonar:sonar -Dsonar.organization=souqtech-team -Dsonar.projectKey=SouqTech-Team_SouqTech -Dsonar.host.url=https://sonarcloud.io'
                         if (isUnix()) {
                            withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                                sh sonarCommand
                            }
                        } else {
                            withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                                bat sonarCommand
                            }
                        }
                    }
                }
            }
        }
        */
    }

    post {
        always {
            // Sauvegarde des rapports de tests JUnit pour affichage dans Jenkins
            junit 'src/backend/target/surefire-reports/*.xml'
        }
        success {
            echo 'Build Backend réussi !'
        }
        failure {
            echo 'Build Backend échoué.'
        }
    }
}

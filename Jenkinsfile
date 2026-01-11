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
        SONAR_TOKEN = credentials('sonar-token') 
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

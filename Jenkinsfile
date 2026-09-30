pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        // Pasta onde esta o projeto Maven (pom.xml)
        PROJECT_DIR = 'Grupo_3_ATDD'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verificar ambiente') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat 'java -version'
                    bat 'mvnw.cmd -version'
                }
            }
        }

        stage('Build') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat 'mvnw.cmd -B clean package -DskipTests'
                }
            }
        }

        stage('Testes') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat 'mvnw.cmd -B test'
                }
            }
        }
    }

    post {
        always {
            // Publica os resultados dos testes (JUnit / Surefire)
            junit allowEmptyResults: true,
                  testResults: "${PROJECT_DIR}/target/surefire-reports/*.xml"
        }
        success {
            echo 'Pipeline finalizado com sucesso!'
        }
        failure {
            echo 'Pipeline falhou. Verifique os logs acima.'
        }
    }
}

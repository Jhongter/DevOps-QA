pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        // Pasta onde está o projeto Cypress
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
                    bat 'dir'
                    bat 'node --version'
                    bat 'npm --version'
                }
            }
        }

        stage('Instalar dependencias') {
            steps {
                dir("${PROJECT_DIR}") {
                    script {
                        if (fileExists('package-lock.json')) {
                            bat 'npm ci'
                        } else {
                            bat 'npm install'
                        }
                    }
                }
            }
        }

        stage('Executar testes Cypress') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat 'npx cypress run'
                }
            }
        }
    }

    post {
        always {
            // Guarda screenshots e videos gerados pelo Cypress
            archiveArtifacts artifacts: "${PROJECT_DIR}/cypress/screenshots/**, ${PROJECT_DIR}/cypress/videos/**",
                             allowEmptyArchive: true
        }
        success {
            echo 'Pipeline finalizado com sucesso!'
        }
        failure {
            echo 'Pipeline falhou. Verifique os logs acima.'
        }
    }
}

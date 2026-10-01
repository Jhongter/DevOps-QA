pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 45, unit: 'MINUTES')
    }

    environment {
        // Pasta onde estao pom.xml, Dockerfile e docker-compose.yml
        PROJECT_DIR  = 'Grupo_3_ATDD'
        COMPOSE_FILE = 'docker-compose.yml'
        BACKEND_PORT = '8081'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Preparar Maven Wrapper') {
            steps {
                writeFile file: "${PROJECT_DIR}/.mvn/wrapper/maven-wrapper.properties",
                          text: '''wrapperVersion=3.3.2
distributionType=only-script
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
'''
            }
        }

        stage('Verificar ambiente') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat 'java -version'
                    bat 'docker version'
                    bat 'docker compose version'
                }
            }
        }

        stage('Testes') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat 'mvnw.cmd -B clean test'
                }
            }
        }

        stage('Docker Build') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat "docker compose -f ${COMPOSE_FILE} build backend"
                }
            }
        }

        stage('Deploy (Docker Compose)') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat "docker compose -f ${COMPOSE_FILE} down --remove-orphans"
                    bat "docker compose -f ${COMPOSE_FILE} up -d postgres pgadmin backend"
                }
            }
        }

        stage('Health Check') {
            steps {
                dir("${PROJECT_DIR}") {
                    script {
                        try {
                            // Espera a porta do backend abrir (ate ~2 minutos)
                            bat '''powershell -NoProfile -Command "$ok=$false; for($i=0; $i -lt 24 -and -not $ok; $i++){ try{ $c=New-Object Net.Sockets.TcpClient('localhost',8081); $c.Close(); $ok=$true }catch{ Start-Sleep 5 } }; if(-not $ok){ exit 1 }"'''
                        } catch (err) {
                            bat "docker compose -f ${COMPOSE_FILE} ps"
                            bat "docker compose -f ${COMPOSE_FILE} logs --tail=100 backend"
                            throw err
                        }
                    }
                    bat "docker compose -f ${COMPOSE_FILE} ps"
                }
            }
        }

        stage('Resumo') {
            steps {
                echo "Backend:  http://localhost:${BACKEND_PORT}"
                echo 'pgAdmin:  http://localhost:5050'
                echo 'Postgres: localhost:5432'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: "${PROJECT_DIR}/target/surefire-reports/*.xml"
        }
        success {
            echo 'Pipeline finalizado com sucesso! Os containers ficaram rodando no Docker.'
        }
        failure {
            echo 'Pipeline falhou. Verifique os logs acima.'
        }
    }
}

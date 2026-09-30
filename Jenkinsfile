pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        // Pasta onde está o projeto de testes
        PROJECT_DIR = 'Grupo_3_ATDD'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Detectar projeto') {
            steps {
                dir("${PROJECT_DIR}") {
                    sh 'echo "Conteúdo de ${PROJECT_DIR}:" && ls -la'
                }
            }
        }

        stage('Instalar dependências') {
            steps {
                dir("${PROJECT_DIR}") {
                    script {
                        if (fileExists('pom.xml')) {
                            sh 'mvn -B -DskipTests clean install'
                        } else if (fileExists('package.json')) {
                            sh 'npm install'
                        } else if (fileExists('requirements.txt')) {
                            sh '''
                                python3 -m venv .venv
                                . .venv/bin/activate
                                pip install -r requirements.txt
                            '''
                        } else {
                            echo 'Nenhum pom.xml, package.json ou requirements.txt encontrado. Pulando instalação.'
                        }
                    }
                }
            }
        }

        stage('Executar testes') {
            steps {
                dir("${PROJECT_DIR}") {
                    script {
                        if (fileExists('pom.xml')) {
                            sh 'mvn -B test'
                        } else if (fileExists('package.json')) {
                            sh 'npm test'
                        } else if (fileExists('requirements.txt')) {
                            sh '''
                                . .venv/bin/activate
                                pytest --junitxml=report.xml
                            '''
                        } else {
                            echo 'Nenhum tipo de projeto reconhecido. Ajuste este stage com o comando de testes.'
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            // Publica relatórios JUnit se existirem (Maven, pytest, etc.)
            junit allowEmptyResults: true,
                  testResults: '**/target/surefire-reports/*.xml, **/report.xml'
        }
        success {
            echo 'Pipeline finalizado com sucesso!'
        }
        failure {
            echo 'Pipeline falhou. Verifique os logs acima.'
        }
    }
}

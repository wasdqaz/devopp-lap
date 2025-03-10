pipeline {
    agent any
    environment {
        MODULE_CHANGED = ''
    }
    stages {
        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = sh(script: "git diff --name-only HEAD~1", returnStdout: true).trim()
                    echo "Changed files:\n${changedFiles}"

                    // Kiểm tra thư mục nào có thay đổi
                    if (changedFiles.contains("spring-petclinic-admin-server/")) {
                        env.MODULE_CHANGED = 'spring-petclinic-admin-server'
                      
                    } else if (changedFiles.contains("spring-petclinic-api-gateway/")) {
                        env.MODULE_CHANGED = 'spring-petclinic-api-gateway'
                      
                    } else if (changedFiles.contains("spring-petclinic-config-server/")) {
                        env.MODULE_CHANGED = 'spring-petclinic-config-server'
                    }

                    } else if (changedFiles.contains("spring-petclinic-customers-service/")) {
                        env.MODULE_CHANGED = 'spring-petclinic-customers-service'
                    }

                    } else if (changedFiles.contains("spring-petclinic-discovery-server/")) {
                        env.MODULE_CHANGED = 'spring-petclinic-discovery-server'
                    }

                    } else if (changedFiles.contains("spring-petclinic-genai-service/")) {
                        env.MODULE_CHANGED = 'spring-petclinic-genai-service'
                    }

                    } else if (changedFiles.contains("spring-petclinic-vets-service/")) {
                        env.MODULE_CHANGED = 'spring-petclinic-vets-service'
                    }

                    } else if (changedFiles.contains("spring-petclinic-visits-service/")) {
                        env.MODULE_CHANGED = 'spring-petclinic-visits-service'
                    }
                }
            }
        }

        stage('Test') {
            when { expression { env.MODULE_CHANGED != '' } }
            steps {
                dir("${env.MODULE_CHANGED}") {
                    sh './mvnw test'
                }
            }
            post {
                always {
                    // Upload test results
                    junit "${env.MODULE_CHANGED}/target/surefire-reports/*.xml"

                    // Upload code coverage (JaCoCo)
                    jacoco execPattern: "${env.MODULE_CHANGED}/target/jacoco.exec"
                }
            }
        }

        stage('Build') {
            when { expression { env.MODULE_CHANGED != '' } }
            steps {
                dir("${env.MODULE_CHANGED}") {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }
    }
}

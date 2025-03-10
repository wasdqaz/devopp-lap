pipeline {
    agent any
    environment {
        MODULES_CHANGED = ''
        REQUIRED_PHASES_PASSED = false
    }
    stages {
        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = sh(script: "git diff --name-only HEAD~1", returnStdout: true).trim().split("\n")
                    echo "Changed files:\n${changedFiles.join('\n')}"

                    def services = [
                        "spring-petclinic-admin-server",
                        "spring-petclinic-api-gateway",
                        "spring-petclinic-config-server",
                        "spring-petclinic-customers-service",
                        "spring-petclinic-discovery-server",
                        "spring-petclinic-genai-service",
                        "spring-petclinic-vets-service",
                        "spring-petclinic-visits-service"
                    ]

                    def affectedServices = services.findAll { service ->
                        changedFiles.any { file -> file.startsWith("${service}/") }
                    }

                    if (affectedServices) {
                        env.MODULES_CHANGED = affectedServices.join(',')
                    }

                    echo "Affected services: ${env.MODULES_CHANGED}"
                }
            }
        }

        stage('Test') {
            when { expression { env.MODULES_CHANGED != '' } }
            steps {
                script {
                    env.MODULES_CHANGED.split(',').each { module ->
                        dir("${module}") {
                            sh './mvnw test'
                        }
                    }
                    env.REQUIRED_PHASES_PASSED = true  // Đánh dấu phase Test đã chạy
                }
            }
            post {
                always {
                    script {
                        env.MODULES_CHANGED.split(',').each { module ->
                            junit "${module}/target/surefire-reports/*.xml"
                            jacoco execPattern: "${module}/target/jacoco.exec"
                        }
                    }
                }
            }
        }

        stage('Build') {
            when { expression { env.MODULES_CHANGED != '' } }
            steps {
                script {
                    env.MODULES_CHANGED.split(',').each { module ->
                        dir("${module}") {
                            sh './mvnw clean package -DskipTests'
                        }
                    }
                    env.REQUIRED_PHASES_PASSED = true  // Đánh dấu phase Build đã chạy
                }
            }
        }
    }
    
    // Kiểm tra nếu cả Test và Build không chạy thì pipeline sẽ fail
    post {
        always {
            script {
                if (!env.REQUIRED_PHASES_PASSED.toBoolean()) {
                    error("Pipeline phải có ít nhất 2 phase: Test và Build.")
                }
            }
        }
    }
}

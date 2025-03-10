pipeline {
    agent any
    environment {
        MODULES_CHANGED = ''
    }
    stages {
        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = sh(script: "git diff --name-only HEAD~1", returnStdout: true).trim()

                    if (changedFiles) {
                        changedFiles = changedFiles.split("\n")
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
                    }

                    echo "Affected services: ${env.MODULES_CHANGED}"
                }
            }
        }

        stage('Test') {
            when { expression { env.MODULES_CHANGED?.trim() } }  // Kiểm tra tránh NULL
            steps {
                script {
                    env.MODULES_CHANGED.split(',').each { module ->
                        dir("${module}") {
                            sh './mvnw test'
                        }
                    }
                }
            }
            post {
                always {
                    script {
                        def modules = env.MODULES_CHANGED?.trim() ? env.MODULES_CHANGED.split(',') : []
                        modules.each { module ->
                            junit "${module}/target/surefire-reports/*.xml"
                            jacoco execPattern: "${module}/target/jacoco.exec"
                        }
                    }
                }
            }
        }

        stage('Build') {
            when { expression { env.MODULES_CHANGED?.trim() } }  // Kiểm tra tránh NULL
            steps {
                script {
                    def modules = env.MODULES_CHANGED?.trim() ? env.MODULES_CHANGED.split(',') : []
                    modules.each { module ->
                        dir("${module}") {
                            sh './mvnw clean package -DskipTests'
                        }
                    }
                }
            }
        }
    }
}

pipeline {
    agent any

    parameters {
        string(name: 'admin-server', defaultValue: 'main', description: 'Branch for admin-server')
        string(name: 'api-gateway', defaultValue: 'main', description: 'Branch for api-gateway')
        string(name: 'config-server', defaultValue: 'main', description: 'Branch for config-server')
        string(name: 'customers-service', defaultValue: 'main', description: 'Branch for customers-service')
        string(name: 'discovery-server', defaultValue: 'main', description: 'Branch for discovery-server')
        string(name: 'genai-service', defaultValue: 'main', description: 'Branch for genai-service')
        string(name: 'vets-service', defaultValue: 'main', description: 'Branch for vets-service')
        string(name: 'visits-service', defaultValue: 'main', description: 'Branch for visits-service')
    }

    environment {
        ADMIN_SERVER_BRANCH      = "${params.'admin-server'}"
        API_GATEWAY_BRANCH       = "${params.'api-gateway'}"
        CONFIG_SERVER_BRANCH     = "${params.'config-server'}"
        CUSTOMERS_SERVICE_BRANCH = "${params.'customers-service'}"
        DISCOVERY_SERVER_BRANCH  = "${params.'discovery-server'}"
        GENAI_SERVICE_BRANCH     = "${params.'genai-service'}"
        VETS_SERVICE_BRANCH      = "${params.'vets-service'}"
        VISITS_SERVICE_BRANCH    = "${params.'visits-service'}"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build & Push Docker Images') {
            steps {
                script {
                    def COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()

                    def modulesList = [
                        [name: "spring-petclinic-admin-server",      envKey: "ADMIN_SERVER_BRANCH"],
                        [name: "spring-petclinic-api-gateway",       envKey: "API_GATEWAY_BRANCH"],
                        [name: "spring-petclinic-config-server",     envKey: "CONFIG_SERVER_BRANCH"],
                        [name: "spring-petclinic-customers-service", envKey: "CUSTOMERS_SERVICE_BRANCH"],
                        [name: "spring-petclinic-discovery-server",  envKey: "DISCOVERY_SERVER_BRANCH"],
                        [name: "spring-petclinic-genai-service",     envKey: "GENAI_SERVICE_BRANCH"],
                        [name: "spring-petclinic-vets-service",      envKey: "VETS_SERVICE_BRANCH"],
                        [name: "spring-petclinic-visits-service",    envKey: "VISITS_SERVICE_BRANCH"]
                    ]

                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )]) {
                        // ✅ FIXED: sử dụng biến đúng cách, không escape
                        sh "docker login -u ${DOCKERHUB_USER} -p ${DOCKERHUB_PASSWORD}"

                        modulesList.each { module ->
                            def branch = env[module.envKey]
                            if (branch) {
                                echo "🔄 Handling ${module.name} on branch ${branch}"

                                dir(module.name) {
                                    // Checkout đúng branch
                                    sh "git checkout ${branch}"

                                    // Build JAR
                                    sh "../mvnw package -DskipTests"

                                    // Build Docker image
                                    def imageTag = "${DOCKERHUB_USER}/${module.name}:${COMMIT_ID}"
                                    echo "🐳 Building Docker image: ${imageTag}"
                                    sh "docker build -t ${imageTag} ."

                                    // Push Docker image
                                    echo "📤 Pushing Docker image: ${imageTag}"
                                    sh "docker push ${imageTag}"
                                }
                            } else {
                                echo "⚠️ No branch configured for ${module.name}, skipping..."
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo '📌 Pipeline execution completed'
        }
        success {
            echo '✅ Pipeline finished successfully'
        }
        failure {
            echo '❌ Pipeline failed. Check logs for errors'
        }
    }
}

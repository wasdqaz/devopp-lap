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
        // Lưu giá trị branch cho mỗi service từ các parameter
        ADMIN_SERVER_BRANCH = "${params.'admin-server'}"
        API_GATEWAY_BRANCH = "${params.'api-gateway'}"
        CONFIG_SERVER_BRANCH = "${params.'config-server'}"
        CUSTOMERS_SERVICE_BRANCH = "${params.'customers-service'}"
        DISCOVERY_SERVER_BRANCH = "${params.'discovery-server'}"
        GENAI_SERVICE_BRANCH = "${params.'genai-service'}"
        VETS_SERVICE_BRANCH = "${params.'vets-service'}"
        VISITS_SERVICE_BRANCH = "${params.'visits-service'}"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
                // Checkout các branch tương ứng cho từng dịch vụ
                sh "git checkout ${env.ADMIN_SERVER_BRANCH}"
                sh "git checkout ${env.API_GATEWAY_BRANCH}"
                sh "git checkout ${env.CONFIG_SERVER_BRANCH}"
                sh "git checkout ${env.CUSTOMERS_SERVICE_BRANCH}"
                sh "git checkout ${env.DISCOVERY_SERVER_BRANCH}"
                sh "git checkout ${env.GENAI_SERVICE_BRANCH}"
                sh "git checkout ${env.VETS_SERVICE_BRANCH}"
                sh "git checkout ${env.VISITS_SERVICE_BRANCH}"
            }
        }
        
        // Bỏ qua phần Detect Changes vì không cần thiết nữa

        stage('Build') {
            steps {
                script {
                    def servicesList = [
                        "spring-petclinic-admin-server",
                        "spring-petclinic-api-gateway",
                        "spring-petclinic-config-server",
                        "spring-petclinic-customers-service",
                        "spring-petclinic-discovery-server",
                        "spring-petclinic-genai-service",
                        "spring-petclinic-vets-service",
                        "spring-petclinic-visits-service"
                    ]
                    
                    // Chỉ build các services có branch được chọn
                    servicesList.each { service ->
                        def serviceBranch = "${service}-BRANCH"
                        def branch = env."${serviceBranch}"

                        if (branch) {
                            echo "Building ${service} from branch ${branch}..."
                            dir(service) {
                                sh "git checkout ${branch}" // Checkout branch đã chọn
                                sh '../mvnw package -DskipTests'
                            }
                        } else {
                            echo "No branch specified for ${service}. Skipping build."
                        }
                    }
                }
            }
        }

        stage('Build & Push Docker Image (CLI)') {
            steps {
                script {
                    def COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    def modulesList = [
                        "spring-petclinic-admin-server",
                        "spring-petclinic-api-gateway",
                        "spring-petclinic-config-server",
                        "spring-petclinic-customers-service",
                        "spring-petclinic-discovery-server",
                        "spring-petclinic-genai-service",
                        "spring-petclinic-vets-service",
                        "spring-petclinic-visits-service"
                    ]

                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )]) {
                        sh "docker login -u \${DOCKERHUB_USER} -p \${DOCKERHUB_PASSWORD}"

                        modulesList.each { module -> 
                            def branch = env."${module}-BRANCH"
                            if (branch) {
                                dir(module) {
                                    def imageTag = "${DOCKERHUB_USER}/${module}:${COMMIT_ID}"
                                    sh "docker build -t ${imageTag} ."
                                    sh "docker push ${imageTag}"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed'
        }
        success {
            echo 'Pipeline finished successfully'
        }
        failure {
            echo 'Pipeline failed. Check logs for errors'
        }
    }
}

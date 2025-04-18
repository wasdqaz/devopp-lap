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
                echo "Checking out the SCM repository"
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    echo "Start building services with specified branches..."
                    def servicesMap = [
                        "spring-petclinic-admin-server": env.ADMIN_SERVER_BRANCH,
                        "spring-petclinic-api-gateway": env.API_GATEWAY_BRANCH,
                        "spring-petclinic-config-server": env.CONFIG_SERVER_BRANCH,
                        "spring-petclinic-customers-service": env.CUSTOMERS_SERVICE_BRANCH,
                        "spring-petclinic-discovery-server": env.DISCOVERY_SERVER_BRANCH,
                        "spring-petclinic-genai-service": env.GENAI_SERVICE_BRANCH,
                        "spring-petclinic-vets-service": env.VETS_SERVICE_BRANCH,
                        "spring-petclinic-visits-service": env.VISITS_SERVICE_BRANCH
                    ]

                    servicesMap.each { service, branch ->
                        echo "Service: ${service}, Branch: ${branch}"  // Log kiểm tra tham số branch
                        
                        if (branch != "main") {
                            echo "Building ${service} from branch ${branch}..."
                            dir(service) {
                                echo "Checking out branch ${branch} for ${service}"
                                sh "git checkout ${branch}"
                                echo "Building ${service} using Maven"
                                sh "../mvnw package -DskipTests"
                            }
                        } else {
                            echo "Skipping ${service} as it is on the main branch."
                        }
                    }
                }
            }
        }

        stage('Build & Push Docker Image (CLI)') {
            steps {
                script {
                    def COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    echo "Commit ID: ${COMMIT_ID}"
                    
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
                        echo "Logging in to Docker Hub"
                        sh "docker login -u \${DOCKERHUB_USER} -p \${DOCKERHUB_PASSWORD}"

                        modulesList.each { module ->
                            def branch = env."${module.replace('-', '_').toUpperCase()}_BRANCH"
                            echo "Building Docker image for ${module} with branch ${branch}"  // Log kiểm tra giá trị nhánh
                            if (branch) {
                                dir(module) {
                                    def imageTag = "${DOCKERHUB_USER}/${module}:${COMMIT_ID}"
                                    echo "Building Docker image for ${module} with tag ${imageTag}"
                                    sh "docker build -t ${imageTag} ."
                                    sh "docker push ${imageTag}"
                                }
                            } else {
                                echo "No branch specified for ${module}, skipping Docker build."
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

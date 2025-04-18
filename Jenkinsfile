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
        DOCKER_IMAGE_TAG = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    }

    stages {
        stage('Checkout SCM') {
            steps {
                echo "Checking out the SCM repository"
                checkout scm
            }
        }

        stage('Build & Package Services') {
            steps {
                script {
                    echo "Start building services with specified branches..."
                    
                    def servicesMap = [
                        "spring-petclinic-admin-server"    : params.'admin-server',
                        "spring-petclinic-api-gateway"     : params.'api-gateway',
                        "spring-petclinic-config-server"   : params.'config-server',
                        "spring-petclinic-customers-service": params.'customers-service',
                        "spring-petclinic-discovery-server": params.'discovery-server',
                        "spring-petclinic-genai-service"   : params.'genai-service',
                        "spring-petclinic-vets-service"    : params.'vets-service',
                        "spring-petclinic-visits-service"  : params.'visits-service'
                    ]

                    servicesMap.each { service, branch ->
                        echo "Service: ${service}, Branch: ${branch}"
                        if (branch != "main") {
                            dir(service) {
                                echo "Checking out branch ${branch} for ${service}"
                                def checkoutStatus = sh(script: "git checkout ${branch}", returnStatus: true)
                                if (checkoutStatus == 0) {
                                    echo "Successfully checked out ${branch} for ${service}"
                                    echo "Building ${service} using Maven"
                                    def buildStatus = sh(script: "../mvnw package -DskipTests", returnStatus: true)
                                    if (buildStatus == 0) {
                                        echo "Successfully built ${service}."
                                    } else {
                                        echo "Build failed for ${service}. Check Maven logs."
                                    }
                                } else {
                                    echo "Failed to checkout branch ${branch} for ${service}. Skipping build."
                                }
                            }
                        } else {
                            echo "Skipping ${service} as it is on the main branch."
                        }
                    }
                }
            }
        }

        stage('Build & Push Docker Images') {
            steps {
                script {
                    def servicesMap = [
                        "spring-petclinic-admin-server"    : params.'admin-server',
                        "spring-petclinic-api-gateway"     : params.'api-gateway',
                        "spring-petclinic-config-server"   : params.'config-server',
                        "spring-petclinic-customers-service": params.'customers-service',
                        "spring-petclinic-discovery-server": params.'discovery-server',
                        "spring-petclinic-genai-service"   : params.'genai-service',
                        "spring-petclinic-vets-service"    : params.'vets-service',
                        "spring-petclinic-visits-service"  : params.'visits-service'
                    ]

                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )]) {
                        echo "Logging in to Docker Hub"
                        sh "docker login -u \${DOCKERHUB_USER} -p \${DOCKERHUB_PASSWORD}"

                        servicesMap.each { service, branch ->
                            echo "Service: ${service}, Branch: ${branch}"
                            if (branch != "main") {
                                dir(service) {
                                    def imageTag = "${DOCKERHUB_USER}/${service}:${env.DOCKER_IMAGE_TAG}"
                                    echo "Building Docker image for ${service} with tag ${imageTag}"
                                    def dockerBuildStatus = sh(script: "docker build -t ${imageTag} .", returnStatus: true)
                                    if (dockerBuildStatus == 0) {
                                        echo "Successfully built Docker image for ${service}."
                                        sh "docker push ${imageTag}"
                                    } else {
                                        echo "Failed to build Docker image for ${service}. Check Docker logs."
                                    }
                                }
                            } else {
                                echo "Skipping Docker build for ${service} (on main branch)."
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

pipeline {
    agent any

    environment {
        DEFAULT_MODULES = "spring-petclinic-admin-server,spring-petclinic-api-gateway,spring-petclinic-config-server,spring-petclinic-customers-service,spring-petclinic-discovery-server,spring-petclinic-genai-service,spring-petclinic-vets-service,spring-petclinic-visits-service"
    }

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

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    // Fallback to initial commit if GIT_PREVIOUS_SUCCESSFUL_COMMIT is null
                    def previousCommit = env.GIT_PREVIOUS_SUCCESSFUL_COMMIT ?: 'HEAD~1'
        
                    echo "Comparing changes between ${previousCommit} and ${env.GIT_COMMIT}"
        
                    def changedFiles = sh(
                        script: "git diff --name-only ${previousCommit} ${env.GIT_COMMIT} -- . ':(exclude)Jenkinsfile' ':(exclude)pom.xml'",
                        returnStdout: true
                    ).trim()
        
                    echo "Changed files:\n${changedFiles}"
                
                    if (changedFiles) {
                        def changedModules = changedFiles
                            .split("\n")
                            .collect { it.split('/')[0] } // Lấy thư mục cấp 1
                            .unique()
                            .findAll { it } // Lọc bỏ giá trị rỗng
                            .join(',')
        
                        env.MODULES_CHANGED = changedModules
                        echo "Modules to process: ${env.MODULES_CHANGED}"
                    } else {
                        echo "No changes detected - stopping pipeline."
                        currentBuild.result = 'ABORTED'
                        return
                    }
                }
            }
        }

        // Build stage updated to process modules based on parameters
        stage('Build') {
            steps {
                script {
                    def servicesList = MODULES_CHANGED.tokenize(',')
                    def selectedServices = []

                    // Check which services were selected via parameters
                    if (params.admin-server != 'main') selectedServices.add('spring-petclinic-admin-server')
                    if (params.api-gateway != 'main') selectedServices.add('spring-petclinic-api-gateway')
                    if (params.config-server != 'main') selectedServices.add('spring-petclinic-config-server')
                    if (params.customers-service != 'main') selectedServices.add('spring-petclinic-customers-service')
                    if (params.discovery-server != 'main') selectedServices.add('spring-petclinic-discovery-server')
                    if (params.genai-service != 'main') selectedServices.add('spring-petclinic-genai-service')
                    if (params.vets-service != 'main') selectedServices.add('spring-petclinic-vets-service')
                    if (params.visits-service != 'main') selectedServices.add('spring-petclinic-visits-service')

                    if (selectedServices.isEmpty()) {
                        echo "No selected services found. Skipping build."
                        return
                    }

                    // Build only the selected services
                    for (service in selectedServices) {
                        echo "Building ${service}..."
                        dir(service) {
                            // Checkout the selected branch for the service
                            sh "git checkout ${params[service]}"
                            sh '../mvnw package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build & Push Docker Image (CLI)') {
            steps {
                script {
                    def COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    def selectedServices = []

                    // Check which services were selected via parameters
                    if (params.admin-server != 'main') selectedServices.add('spring-petclinic-admin-server')
                    if (params.api-gateway != 'main') selectedServices.add('spring-petclinic-api-gateway')
                    if (params.config-server != 'main') selectedServices.add('spring-petclinic-config-server')
                    if (params.customers-service != 'main') selectedServices.add('spring-petclinic-customers-service')
                    if (params.discovery-server != 'main') selectedServices.add('spring-petclinic-discovery-server')
                    if (params.genai-service != 'main') selectedServices.add('spring-petclinic-genai-service')
                    if (params.vets-service != 'main') selectedServices.add('spring-petclinic-vets-service')
                    if (params.visits-service != 'main') selectedServices.add('spring-petclinic-visits-service')

                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )]) {
                        sh "docker login -u \${DOCKERHUB_USER} -p \${DOCKERHUB_PASSWORD}"

                        selectedServices.each { module ->
                            dir(module) {
                                // Sửa đổi tag image thành ${module}:${COMMIT_ID}
                                def imageTag = "${DOCKERHUB_USER}/${module}:${COMMIT_ID}"
                                sh "docker build -t ${imageTag} ."
                                sh "docker push ${imageTag}"
                            }
                        }
                    }
                }
            }
        }

        // stage('Deploy to Kubernetes') {
        //     steps {
        //         script {
        //             def COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
        //             def modulesList = env.MODULES_CHANGED.split(',')

        //             modulesList.each { module -> 
        //                 echo "Deploying ${module} to Kubernetes with image tag: ${COMMIT_ID}"

        //                 // Update image tag in deployment YAML
        //                 sh """
        //                 sed -i 's|image: ${DOCKER_HUB_USERNAME}/${module}:.*|image: ${DOCKER_HUB_USERNAME}/${module}:${COMMIT_ID}|' k8s/${module}/deployment.yaml
        //                 """

        //                 // Apply to Kubernetes
        //                 sh "kubectl apply -f k8s/${module}/deployment.yaml"
        //             }
        //         }
        //     }
        // }

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

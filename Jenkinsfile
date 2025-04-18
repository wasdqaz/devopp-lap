pipeline {
    agent any

    environment {
        DEFAULT_MODULES = "spring-petclinic-admin-server,spring-petclinic-api-gateway,spring-petclinic-config-server,spring-petclinic-customers-service,spring-petclinic-discovery-server,spring-petclinic-genai-service,spring-petclinic-vets-service,spring-petclinic-visits-service"
    }

    parameters {
        string(name: 'MODULES_TO_DEPLOY', defaultValue: '', description: 'Comma-separated list of services to deploy (e.g., vets-service,customers-service)')
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

        stage('Build') {
            steps {
                script {
                    def servicesList = MODULES_CHANGED.tokenize(',')

                    if (servicesList.isEmpty()) {
                        echo "No changed services found. Skipping build."
                        return
                    }

                    for (service in servicesList) {
                        echo " Building ${service}..."
                        dir(service) {
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
                    def modulesList = env.MODULES_CHANGED.split(',')
                    
                    // Add check for MODULES_TO_DEPLOY parameter
                    def deployList = params.MODULES_TO_DEPLOY ? params.MODULES_TO_DEPLOY.split(',') : modulesList

                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )]) {
                        sh "docker login -u \${DOCKERHUB_USER} -p \${DOCKERHUB_PASSWORD}"

                        deployList.each { module ->
                            if (modulesList.contains(module)) {
                                def imageTag = "${DOCKERHUB_USER}/${module}:${COMMIT_ID}"
                                echo "Building and pushing image for ${module} with tag ${imageTag}..."
                                dir(module) {
                                    sh "docker build -t ${imageTag} ."
                                    sh "docker push ${imageTag}"
                                }
                            } else {
                                def defaultImageTag = "${DOCKERHUB_USER}/${module}:latest"
                                echo "Pushing default image for ${module} with tag ${defaultImageTag}..."
                                dir(module) {
                                    sh "docker push ${defaultImageTag}"
                                }
                            }
                        }
                    }
                }
            }
        }

        // Optional stage to deploy to Kubernetes, if you need it
        // stage('Deploy to Kubernetes') {
        //     steps {
        //         script {
        //             def COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
        //             def modulesList = env.MODULES_CHANGED.split(',')
        
        //             modulesList.each { module -> 
        //                 echo "Deploying ${module} with image tag ${COMMIT_ID}"
        //                 sh "kubectl set image deployment/${module} ${module}=${DOCKERHUB_USER}/${module}:${COMMIT_ID} --record"
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

pipeline {
    agent any

    environment {
        DEFAULT_MODULES = "spring-petclinic-admin-server,spring-petclinic-api-gateway,spring-petclinic-config-server,spring-petclinic-customers-service,spring-petclinic-discovery-server,spring-petclinic-genai-service,spring-petclinic-vets-service,spring-petclinic-visits-service"
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

        stage("Use developer_build.sh file") {
            steps {
                sh './developer_build.sh'
            }
        }



        // stage('Test') {
        //     steps {
        //         script {
        //             def modulesList = env.MODULES_CHANGED.split(',')

        //             modulesList.each { module ->
        //                 dir(module) {
        //                     echo "Running tests for: ${module}"
        //                     // Run JaCoCo agent during test phase
        //                     sh "../mvnw clean verify -Pspringboot"
        //                 }
        //             }
        //         }
        //     }
        //     post {
        //         always {
        //             script {
        //                 def modulesList = env.MODULES_CHANGED.split(',')

        //                 modulesList.each { module ->
        //                     dir(module) {
        //                         echo "📊 Analyzing JaCoCo coverage for: ${module}"
        //                         // Dùng jacoco plugin trong module tương ứng
        //                         jacoco(
        //                             execPattern: 'target/jacoco.exec',
        //                             classPattern: 'target/classes',
        //                             sourcePattern: 'src/main/java',
        //                             exclusionPattern: 'src/test*'
        //                         )
        //                     }
        //                 }
        //             }
        //         }
        //     }
        // }

        stage('Build') {
            steps {
                script {
                    def servicesList = MODULES_CHANGED.tokenize(',')

                    if (servicesList.isEmpty()) {
                        echo "ℹ️ No changed services found. Skipping build."
                        return
                    }

                    for (service in servicesList) {
                        echo "🏗️ Building ${service}..."
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

                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )]) {
                        sh "docker login -u \${DOCKERHUB_USER} -p \${DOCKERHUB_PASSWORD}"

                        
                        modulesList.each { module ->
                            dir(module) {
                                def imageTag = "${DOCKERHUB_USER}/${module}:${COMMIT_ID}"
                                sh "docker build -t ${imageTag} ."
                                sh "docker push ${imageTag}"
                            }
                        }// module
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
        //                 echo "🔁 Deploying ${module} to Kubernetes with image tag: ${COMMIT_ID}"

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

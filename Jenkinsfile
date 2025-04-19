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
                            .collect { it.split('/')[0] }
                            .unique()
                            .findAll { it } // remove empty
                            .join(',')

                        env.MODULES_CHANGED = changedModules
                        echo "Modules to process: ${env.MODULES_CHANGED}"
                    } else {
                        echo "No changes detected - stopping pipeline."
                        currentBuild.result = 'ABORTED'
                        error("No changes detected")
                    }
                }
            }
        }

        stage('Build') {
            when {
                expression { return env.MODULES_CHANGED }
            }
            steps {
                script {
                    def modulesList = env.MODULES_CHANGED.tokenize(',')

                    for (module in modulesList) {
                        echo "Building ${module}..."
                        dir(module) {
                            sh '../mvnw package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build & Push Docker Image (CLI)') {
            when {
                expression { return env.MODULES_CHANGED }
            }
            steps {
                script {
                    def COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    def modulesList = env.MODULES_CHANGED.tokenize(',')

                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )]) {
                        sh "docker login -u ${DOCKERHUB_USER} -p ${DOCKERHUB_PASSWORD}"

                        for (module in modulesList) {
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

        // Optional stage để deploy (comment lại nếu chưa dùng)
        // stage('Deploy to Kubernetes') {
        //     steps {
        //         script {
        //             def COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
        //             def modulesList = env.MODULES_CHANGED.tokenize(',')

        //             for (module in modulesList) {
        //                 echo "Deploying ${module} with image tag ${COMMIT_ID}"
        //                 sh """
        //                 sed -i 's|image: .*/${module}:.*|image: ${DOCKERHUB_USER}/${module}:${COMMIT_ID}|' k8s/${module}/deployment.yaml
        //                 kubectl apply -f k8s/${module}/deployment.yaml
        //                 """
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

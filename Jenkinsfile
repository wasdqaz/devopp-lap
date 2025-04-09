pipeline {
    agent any

    environment {
        DEFAULT_MODULES = "spring-petclinic-admin-server,spring-petclinic-api-gateway,spring-petclinic-config-server,spring-petclinic-customers-service,spring-petclinic-discovery-server,spring-petclinic-genai-service,spring-petclinic-vets-service,spring-petclinic-visits-service"
        DOCKER_HUB_USERNAME = "soulgalaxy"
        DOCKER_HUB_CREDENTIALS_ID = "docker-hub-credentials"
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



        stage('Test') {
            steps {
                script {
                    def modulesList = env.MODULES_CHANGED.split(',')

                    modulesList.each { module ->
                        dir(module) {
                            echo "Running tests for: ${module}"
                            // Run JaCoCo agent during test phase
                            sh "../mvnw clean verify -Pspringboot"
                        }
                    }
                }
            }
            post {
                always {
                    script {
                        def modulesList = env.MODULES_CHANGED.split(',')

                        modulesList.each { module ->
                            dir(module) {
                                echo "📊 Analyzing JaCoCo coverage for: ${module}"
                                // Dùng jacoco plugin trong module tương ứng
                                jacoco(
                                    execPattern: 'target/jacoco.exec',
                                    classPattern: 'target/classes',
                                    sourcePattern: 'src/main/java',
                                    exclusionPattern: 'src/test*'
                                )
                            }
                        }
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    def modulesList = env.MODULES_CHANGED.split(',')

                    modulesList.each { module ->
                        dir(module) {
                            echo "Building module: ${module}"
                            sh "${WORKSPACE}/mvnw package"
                        }
                    }
                }
            }
        }

        stage('Build & Push Docker Image (Plugin)') {
            steps {
                script {
                    def COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    def modulesList = env.MODULES_CHANGED.split(',')

                    docker.withRegistry("https://index.docker.io/v1/", DOCKER_HUB_CREDENTIALS_ID) {
                        modulesList.each { module ->
                            dir(module) {
                                def image = docker.build("${DOCKER_HUB_USERNAME}/${module}:${COMMIT_ID}")
                                image.push()
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

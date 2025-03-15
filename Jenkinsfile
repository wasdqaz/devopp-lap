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
                    // Kiểm tra nếu `GIT_PREVIOUS_SUCCESSFUL_COMMIT` bị null, thay bằng HEAD~1
                    def previousCommit = env.GIT_PREVIOUS_SUCCESSFUL_COMMIT ?: 'HEAD~1'
                    def changedFiles = sh(script: "git diff --name-only ${previousCommit} ${env.GIT_COMMIT} -- . ':(exclude)Jenkinsfile' ':(exclude)pom.xml'", returnStdout: true).trim()
        
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
                            // Specify JaCoCo report pattern
                                    jacoco(
                                        execPattern: 'spring-petclinic-api-gateway/target/jacoco.exec',
                                        classPattern: 'spring-petclinic-api-gateway/target/classes',
                                        sourcePattern: 'spring-petclinic-api-gateway/src/main/java',
                                        exclusionPattern: 'spring-petclinic-api-gateway/src/test*'
                                    )


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

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
                    def changedFiles = sh(script: "git diff --name-only origin/main", returnStdout: true).trim()
                    echo "Changed files:\n${changedFiles}"
        
                    def changedModules = changedFiles
                        .split("\n")
                        .collect { it.split('/')[0] }  // Lấy thư mục cấp 1 (tên module)
                        .unique()
                        .findAll { it && it != 'Jenkinsfile' }  // Loại bỏ Jenkinsfile nếu bị nhận diện nhầm
                        .join(',')
        
                    env.MODULES_CHANGED = changedModules ?: env.DEFAULT_MODULES
                    echo "Modules to process: ${env.MODULES_CHANGED}"
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
                            sh "${WORKSPACE}/mvnw test --fail-at-end -Dmaven.test.failure.ignore=true"

                            // Debug: Liệt kê test reports
                            sh "ls -la target/surefire-reports/ || true"
                        }
                    }
                }
            }
            post {
                always {
                    script {
                        def modulesList = env.MODULES_CHANGED.split(',')

                        modulesList.each { module ->
                            echo "Uploading test results for: ${module}"
                            junit allowEmptyResults: true, testResults: "${module}/target/surefire-reports/*.xml"

                            echo "Uploading code coverage for: ${module}"
                            jacoco execPattern: "${module}/target/jacoco.exec"
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
                        sh "${WORKSPACE}/mvnw clean package"
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

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
                        if (fileExists("${module}/pom.xml")) {  // Kiểm tra module có pom.xml không
                            if (fileExists("${module}/src/test")) {  // Chỉ chạy test nếu có src/test
                                dir(module) {
                                    echo "Running tests for module: ${module}"
                                    sh './mvnw test'
        
                                    junit '**/target/surefire-reports/*.xml'
                                    publishCoverage adapters: [jacocoAdapter('**/target/site/jacoco/jacoco.xml')]
                                }
                            } else {
                                echo "Skipping tests for ${module} (No tests found)"
                            }
                        } else {
                            echo "Skipping test for ${module} (No pom.xml found)"
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
                            sh './mvnw clean package'
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def modulesList = env.MODULES_CHANGED.split(',')
                    
                    modulesList.each { module ->
                        dir(module) {
                            echo "Deploying module: ${module}"
                            sh './deploy.sh'  // Adjust this to match your deployment process
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

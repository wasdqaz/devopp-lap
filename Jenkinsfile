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
                    // Get changed files in last commit (modify this command if needed)
                    def changedFiles = sh(script: "git diff --name-only origin/main", returnStdout: true).trim()
                    echo "Changed files:\n${changedFiles}"

                    // Extract changed modules based on directory structure
                    def changedModules = changedFiles
                        .split("\n")
                        .collect { it.split('/')[0] }
                        .unique()
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
                            echo "Running tests for module: ${module}"
                            sh './mvnw test'

                            // Archive test results
                            junit '**/target/surefire-reports/*.xml'

                            // Upload code coverage report (adjust path if needed)
                            publishCoverage adapters: [jacocoAdapter('**/target/site/jacoco/jacoco.xml')]
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

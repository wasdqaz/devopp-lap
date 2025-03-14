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
                    // Use git diff with pathspec filtering to exclude Jenkinsfile and pom.xml
                    def changedFiles = sh(script: "git diff --name-only ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT} ${env.GIT_COMMIT} -- . ':(exclude)Jenkinsfile' ':(exclude)pom.xml'", returnStdout: true).trim()
                    
                    echo "Changed files:\n${changedFiles}"
                    
                    if (changedFiles) {
                        def changedModules = changedFiles
                            .split("\n")
                            .collect { it.split('/')[0] }  // Extract top-level directory (module name)
                            .unique()
                            .findAll { it }  // Filter out empty values
                            .join(',')
        
                        env.MODULES_CHANGED = changedModules
                        echo "Modules to process: ${env.MODULES_CHANGED}"
                    } else {
                        // Stop pipeline gracefully if no changes are detected
                        echo "No changes detected - stopping pipeline."
                        currentBuild.result = 'ABORTED'
                        return // Exit the stage without marking it as failed
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

        stage('Check Coverage') {
            steps {
                script {
                    def modulesList = env.MODULES_CHANGED.split(',')
        
                    modulesList.each { module ->
                        def coverage = sh(script: '''
                            grep -oP '(?<=<counter type="INSTRUCTION" missed="\\d+" covered=")\\d+' ''' + module + '''/target/site/jacoco/jacoco.xml | 
                            awk '{sum+=$1} END {print sum}'
                        ''', returnStdout: true).trim()
        
                        def total = sh(script: '''
                            grep -oP '(?<=<counter type="INSTRUCTION" missed=")\\d+' ''' + module + '''/target/site/jacoco/jacoco.xml | 
                            awk '{sum+=$1} END {print sum}'
                        ''', returnStdout: true).trim()
        
                        def percentage = (coverage.toInteger() * 100) / (coverage.toInteger() + total.toInteger())
        
                        echo "Test Coverage for ${module}: ${percentage}%"
        
                        if (percentage < 70) {
                            error("Test coverage for ${module} is below 70%! Failing the build.")
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

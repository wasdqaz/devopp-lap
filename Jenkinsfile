pipeline {
    agent any
    
    options {
        // Bỏ qua checkout mặc định để xử lý thủ công
        skipDefaultCheckout(true)
        // Thêm timestamps vào console output
        timestamps()
        // Giữ lại tối đa 10 lần build gần nhất
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    
    stages {
        stage('Checkout') {
            steps {
                // Dọn dẹp workspace trước khi checkout
                cleanWs()
                // Checkout code từ SCM
                checkout scm
            }
        }
        
        // Kiểm tra thay đổi trong các file root ảnh hưởng đến tất cả services
        stage('Check Root Changes') {
            steps {
                script {
                    // Flag để xác định có build tất cả services hay không
                    env.BUILD_ALL = "false"
                    
                    // Nếu đây là lần build đầu tiên hoặc branch mới, build tất cả
                    if (env.GIT_PREVIOUS_SUCCESSFUL_COMMIT == null) {
                        env.BUILD_ALL = "true"
                        echo "Lần build đầu tiên hoặc branch mới - sẽ build tất cả services"
                    } else {
                        // Kiểm tra các file quan trọng ở root có thay đổi không
                        def changedFiles = sh(
                            script: "git diff --name-only ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT} ${env.GIT_COMMIT}",
                            returnStdout: true
                        ).trim()
                        
                        if (changedFiles =~ /^pom\.xml$/ || changedFiles =~ /^Jenkinsfile$/ || changedFiles =~ /^docker-compose\.yml$/) {
                            env.BUILD_ALL = "true"
                            echo "Các file root đã thay đổi - sẽ build tất cả services"
                        }
                    }
                }
            }
        }
        
        // Chạy song song các pipeline cho từng service
        stage('Parallel Service Pipelines') {
            parallel {
                // API Gateway Service
                stage('API Gateway Pipeline') {
                    when {
                        anyOf {
                            changeset "spring-petclinic-api-gateway/**"
                            expression { return env.BUILD_ALL == "true" }
                        }
                    }
                    stages {
                        stage('Test') {
                            steps {
                                dir('spring-petclinic-api-gateway') {
                                    sh 'mvn clean test'
                                }
                            }
                            post {
                                always {
                                    junit 'spring-petclinic-api-gateway/target/surefire-reports/*.xml'
                                    jacoco(
                                        execPattern: 'spring-petclinic-api-gateway/target/jacoco.exec',
                                        classPattern: 'spring-petclinic-api-gateway/target/classes',
                                        sourcePattern: 'spring-petclinic-api-gateway/src/main/java',
                                        exclusionPattern: 'spring-petclinic-api-gateway/src/test*'
                                    )
                                }
                            }
                        }
                        stage('Build') {
                            steps {
                                dir('spring-petclinic-api-gateway') {
                                    sh 'mvn package -DskipTests'
                                }
                            }
                            post {
                                success {
                                    archiveArtifacts artifacts: 'spring-petclinic-api-gateway/target/*.jar', fingerprint: true
                                }
                            }
                        }
                    }
                }
                
                // Customers Service
                stage('Customers Service Pipeline') {
                    when {
                        anyOf {
                            changeset "spring-petclinic-customers-service/**"
                            expression { return env.BUILD_ALL == "true" }
                        }
                    }
                    stages {
                        stage('Test') {
                            steps {
                                dir('spring-petclinic-customers-service') {
                                    sh 'mvn clean test'
                                }
                            }
                            post {
                                always {
                                    junit 'spring-petclinic-customers-service/target/surefire-reports/*.xml'
                                    jacoco(
                                        execPattern: 'spring-petclinic-customers-service/target/jacoco.exec',
                                        classPattern: 'spring-petclinic-customers-service/target/classes',
                                        sourcePattern: 'spring-petclinic-customers-service/src/main/java',
                                        exclusionPattern: 'spring-petclinic-customers-service/src/test*'
                                    )
                                }
                            }
                        }
                        stage('Build') {
                            steps {
                                dir('spring-petclinic-customers-service') {
                                    sh 'mvn package -DskipTests'
                                }
                            }
                            post {
                                success {
                                    archiveArtifacts artifacts: 'spring-petclinic-customers-service/target/*.jar', fingerprint: true
                                }
                            }
                        }
                    }
                }
                
                // Vets Service
                stage('Vets Service Pipeline') {
                    when {
                        anyOf {
                            changeset "spring-petclinic-vets-service/**"
                            expression { return env.BUILD_ALL == "true" }
                        }
                    }
                    stages {
                        stage('Test') {
                            steps {
                                dir('spring-petclinic-vets-service') {
                                    sh 'mvn clean test'
                                }
                            }
                            post {
                                always {
                                    junit 'spring-petclinic-vets-service/target/surefire-reports/*.xml'
                                    jacoco(
                                        execPattern: 'spring-petclinic-vets-service/target/jacoco.exec',
                                        classPattern: 'spring-petclinic-vets-service/target/classes',
                                        sourcePattern: 'spring-petclinic-vets-service/src/main/java',
                                        exclusionPattern: 'spring-petclinic-vets-service/src/test*'
                                    )
                                }
                            }
                        }
                        stage('Build') {
                            steps {
                                dir('spring-petclinic-vets-service') {
                                    sh 'mvn package -DskipTests'
                                }
                            }
                            post {
                                success {
                                    archiveArtifacts artifacts: 'spring-petclinic-vets-service/target/*.jar', fingerprint: true
                                }
                            }
                        }
                    }
                }
                
                // Visits Service
                stage('Visits Service Pipeline') {
                    when {
                        anyOf {
                            changeset "spring-petclinic-visits-service/**"
                            expression { return env.BUILD_ALL == "true" }
                        }
                    }
                    stages {
                        stage('Test') {
                            steps {
                                dir('spring-petclinic-visits-service') {
                                    sh 'mvn clean test'
                                }
                            }
                            post {
                                always {
                                    junit 'spring-petclinic-visits-service/target/surefire-reports/*.xml'
                                    jacoco(
                                        execPattern: 'spring-petclinic-visits-service/target/jacoco.exec',
                                        classPattern: 'spring-petclinic-visits-service/target/classes',
                                        sourcePattern: 'spring-petclinic-visits-service/src/main/java',
                                        exclusionPattern: 'spring-petclinic-visits-service/src/test*'
                                    )
                                }
                            }
                        }
                        stage('Build') {
                            steps {
                                dir('spring-petclinic-visits-service') {
                                    sh 'mvn package -DskipTests'
                                }
                            }
                            post {
                                success {
                                    archiveArtifacts artifacts: 'spring-petclinic-visits-service/target/*.jar', fingerprint: true
                                }
                            }
                        }
                    }
                }
                
                // Config Server
                stage('Config Server Pipeline') {
                    when {
                        anyOf {
                            changeset "spring-petclinic-config-server/**"
                            expression { return env.BUILD_ALL == "true" }
                        }
                    }
                    stages {
                        stage('Test') {
                            steps {
                                dir('spring-petclinic-config-server') {
                                    sh 'mvn clean test'
                                }
                            }
                            post {
                                always {
                                    junit 'spring-petclinic-config-server/target/surefire-reports/*.xml'
                                    jacoco(
                                        execPattern: 'spring-petclinic-config-server/target/jacoco.exec',
                                        classPattern: 'spring-petclinic-config-server/target/classes',
                                        sourcePattern: 'spring-petclinic-config-server/src/main/java',
                                        exclusionPattern: 'spring-petclinic-config-server/src/test*'
                                    )
                                }
                            }
                        }
                        stage('Build') {
                            steps {
                                dir('spring-petclinic-config-server') {
                                    sh 'mvn package -DskipTests'
                                }
                            }
                            post {
                                success {
                                    archiveArtifacts artifacts: 'spring-petclinic-config-server/target/*.jar', fingerprint: true
                                }
                            }
                        }
                    }
                }
                
                // Discovery Server
                stage('Discovery Server Pipeline') {
                    when {
                        anyOf {
                            changeset "spring-petclinic-discovery-server/**"
                            expression { return env.BUILD_ALL == "true" }
                        }
                    }
                    stages {
                        stage('Test') {
                            steps {
                                dir('spring-petclinic-discovery-server') {
                                    sh 'mvn clean test'
                                }
                            }
                            post {
                                always {
                                    junit 'spring-petclinic-discovery-server/target/surefire-reports/*.xml'
                                    jacoco(
                                        execPattern: 'spring-petclinic-discovery-server/target/jacoco.exec',
                                        classPattern: 'spring-petclinic-discovery-server/target/classes',
                                        sourcePattern: 'spring-petclinic-discovery-server/src/main/java',
                                        exclusionPattern: 'spring-petclinic-discovery-server/src/test*'
                                    )
                                }
                            }
                        }
                        stage('Build') {
                            steps {
                                dir('spring-petclinic-discovery-server') {
                                    sh 'mvn package -DskipTests'
                                }
                            }
                            post {
                                success {
                                    archiveArtifacts artifacts: 'spring-petclinic-discovery-server/target/*.jar', fingerprint: true
                                }
                            }
                        }
                    }
                }
                
                // Admin Server
                stage('Admin Server Pipeline') {
                    when {
                        anyOf {
                            changeset "spring-petclinic-admin-server/**"
                            expression { return env.BUILD_ALL == "true" }
                        }
                    }
                    stages {
                        stage('Test') {
                            steps {
                                dir('spring-petclinic-admin-server') {
                                    sh 'mvn clean test'
                                }
                            }
                            post {
                                always {
                                    junit 'spring-petclinic-admin-server/target/surefire-reports/*.xml'
                                    jacoco(
                                        execPattern: 'spring-petclinic-admin-server/target/jacoco.exec',
                                        classPattern: 'spring-petclinic-admin-server/target/classes',
                                        sourcePattern: 'spring-petclinic-admin-server/src/main/java',
                                        exclusionPattern: 'spring-petclinic-admin-server/src/test*'
                                    )
                                }
                            }
                        }
                        stage('Build') {
                            steps {
                                dir('spring-petclinic-admin-server') {
                                    sh 'mvn package -DskipTests'
                                }
                            }
                            post {
                                success {
                                    archiveArtifacts artifacts: 'spring-petclinic-admin-server/target/*.jar', fingerprint: true
                                }
                            }
                        }
                    }
                }
                
                // GenAI Service
                stage('GenAI Service Pipeline') {
                    when {
                        anyOf {
                            changeset "spring-petclinic-genai-service/**"
                            expression { return env.BUILD_ALL == "true" }
                        }
                    }
                    stages {
                        stage('Test') {
                            steps {
                                dir('spring-petclinic-genai-service') {
                                    sh 'mvn clean test'
                                }
                            }
                            post {
                                always {
                                    junit 'spring-petclinic-genai-service/target/surefire-reports/*.xml'
                                    jacoco(
                                        execPattern: 'spring-petclinic-genai-service/target/jacoco.exec',
                                        classPattern: 'spring-petclinic-genai-service/target/classes',
                                        sourcePattern: 'spring-petclinic-genai-service/src/main/java',
                                        exclusionPattern: 'spring-petclinic-genai-service/src/test*'
                                    )
                                }
                            }
                        }
                        stage('Build') {
                            steps {
                                dir('spring-petclinic-genai-service') {
                                    sh 'mvn package -DskipTests'
                                }
                            }
                            post {
                                success {
                                    archiveArtifacts artifacts: 'spring-petclinic-genai-service/target/*.jar', fingerprint: true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Dọn dẹp workspace sau khi hoàn thành
            cleanWs()
        }
        success {
            echo 'Build thành công!'
        }
        failure {
            echo 'Build thất bại!'
        }
    }
}

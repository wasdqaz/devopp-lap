pipeline {
    agent any

    // Cấu hình để Jenkins quét và chạy pipeline cho từng branch
    triggers {
        // Sử dụng trigger tự động khi có thay đổi trong repo
        // Bạn có thể cấu hình thời gian quét trong Jenkins UI
        // tại phần "Scan Repository Triggers"
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout code từ Git
                checkout scm
            }
        }
        stage('Build') {
            when {
                changeset "**/vets-service/*.*"
            }
            steps {
                // Xây dựng dịch vụ vets-service
                dir('vets-service') {
                    sh 'mvn clean package'
                }
            }
        }
        stage('Test') {
            when {
                changeset "**/vets-service/*.*"
            }
            steps {
                // Chạy test và upload kết quả test
                dir('vets-service') {
                    sh 'mvn test'
                    // Upload kết quả test lên Jenkins
                    junit 'target/surefire-reports/*.xml'
                    // Cấu hình độ phủ test
                    jacoco(
                        execPattern: 'target/jacoco.exec',
                        classPattern: 'target/classes',
                        sourcePattern: 'src/main/java',
                        inclusionPattern: '**/*',
                        exclusionPattern: '**/*Test*.*'
                    )
                }
            }
        }
        stage('Build') {
            when {
                changeset "**/another-service/*.*"
            }
            steps {
                // Xây dựng dịch vụ khác
                dir('another-service') {
                    sh 'mvn clean package'
                }
            }
        }
        stage('Test') {
            when {
                changeset "**/another-service/*.*"
            }
            steps {
                // Chạy test và upload kết quả test cho dịch vụ khác
                dir('another-service') {
                    sh 'mvn test'
                    junit 'target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: 'target/jacoco.exec',
                        classPattern: 'target/classes',
                        sourcePattern: 'src/main/java',
                        inclusionPattern: '**/*',
                        exclusionPattern: '**/*Test*.*'
                    )
                }
            }
        }
    }
}

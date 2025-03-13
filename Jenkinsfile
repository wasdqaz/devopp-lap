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
        stage('Build and Test') {
            steps {
                // Lặp qua các module và kiểm tra thay đổi
                def modules = ['vets-service', 'another-service']
                for (module in modules) {
                    if (changeset "**/${module}/*.*") {
                        dir(module) {
                            // Build module
                            sh 'mvn clean package'
                            // Chạy test và upload kết quả test
                            sh 'mvn test'
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
            }
        }
    }
}

pipeline {
    agent any

    environment {
        DEFAULT_MODULES = "spring-petclinic-admin-server,spring-petclinic-api-gateway,spring-petclinic-config-server,spring-petclinic-customers-service,spring-petclinic-discovery-server,spring-petclinic-genai-service,spring-petclinic-vets-service,spring-petclinic-visits-service"
    }

    parameters {
        script {
            def modules = environment.DEFAULT_MODULES.tokenize(',')
            def paramsList = []
            modules.each { module ->
                paramsList.add(string(name: module.trim(), defaultValue: 'main', description: "Branch to build for ${module.trim()}"))
            }
            return paramsList
        }
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }
        
       // stage('Detect Changes') {
       //      steps {
       //          script {
                    // Fallback to initial commit if GIT_PREVIOUS_SUCCESSFUL_COMMIT is null
       //              def previousCommit = env.GIT_PREVIOUS_SUCCESSFUL_COMMIT ?: 'HEAD~1'
        
       //              echo "Comparing changes between ${previousCommit} and ${env.GIT_COMMIT}"
        
       //              def changedFiles = sh(
       //                  script: "git diff --name-only ${previousCommit} ${env.GIT_COMMIT} -- . ':(exclude)Jenkinsfile' ':(exclude)pom.xml'",
       //                  returnStdout: true
       //              ).trim()
        
       //              echo "Changed files:\n${changedFiles}"
                
       //              if (changedFiles) {
       //                  def changedModules = changedFiles
       //                      .split("\n")
       //                      .collect { it.split('/')[0] } // Lấy thư mục cấp 1
       //                      .unique()
       //                      .findAll { it } // Lọc bỏ giá trị rỗng
       //                      .join(',')
        
       //                  env.MODULES_CHANGED = changedModules
       //                  echo "Modules to process: ${env.MODULES_CHANGED}"
       //              } else {
       //                  echo "No changes detected - stopping pipeline."
       //                  currentBuild.result = 'ABORTED'
       //                  return
       //              }
       //          }
       //      }
       //  }



        // stage('Test') {
        //     steps {
        //         script {
        //             def modulesList = env.MODULES_CHANGED.split(',')

        //             modulesList.each { module ->
        //                 dir(module) {
        //                     echo "Running tests for: ${module}"
        //                     // Run JaCoCo agent during test phase
        //                     sh "../mvnw clean verify -Pspringboot"
        //                 }
        //             }
        //         }
        //     }
        //     post {
        //         always {
        //             script {
        //                 def modulesList = env.MODULES_CHANGED.split(',')

        //                 modulesList.each { module ->
        //                     dir(module) {
        //                         echo "📊 Analyzing JaCoCo coverage for: ${module}"
        //                         // Dùng jacoco plugin trong module tương ứng
        //                         jacoco(
        //                             execPattern: 'target/jacoco.exec',
        //                             classPattern: 'target/classes',
        //                             sourcePattern: 'src/main/java',
        //                             exclusionPattern: 'src/test*'
        //                         )
        //                     }
        //                 }
        //             }
        //         }
        //     }
        // }

        // stage('Build') {
        //     steps {
        //         script {
        //             def servicesList = MODULES_CHANGED.tokenize(',')

        //             if (servicesList.isEmpty()) {
        //                 echo "No changed services found. Skipping build."
        //                 return
        //             }

        //             for (service in servicesList) {
        //                 echo " Building ${service}..."
        //                 dir(service) {
        //                     sh '../mvnw package -DskipTests'
        //                 }
        //             }
        //         }
        //     }
        // }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
        
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        sh "docker login -u ${DOCKERHUB_USER} -p ${DOCKERHUB_PASSWORD}"
        
                        env.DEFAULT_MODULES.tokenize(',').each { module ->
                            def targetBranch = params."${module}"?.trim()
                            echo "Parameter '${module}': ${targetBranch}"
                            def imageName = "${DOCKERHUB_USER}/${module}"
                            def imageTag
        
                            if (targetBranch && targetBranch != 'main' && !targetBranch.isEmpty()) {
                                echo "Condition 'targetBranch && targetBranch != 'main' && !targetBranch.isEmpty()' is true. targetBranch: '${targetBranch}'"
                                // Checkout branch cụ thể và build service đó
                                checkout([$class: 'GitSCM', branches: [[name: targetBranch]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: scm.userRemoteConfigs[0].url]]])
                                dir(module) {
                                    echo "Building service: ${module} from branch: ${targetBranch}"
                                    sh '../mvnw package -DskipTests'
                                    // Tag image với commit ID của HEAD sau khi build
                                    def builtCommitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                                    imageTag = "${imageName}:${builtCommitId}"
                                    sh "docker build -t ${imageTag} ."
                                    sh "docker push ${imageTag}"
                                }
                            } else {
                                imageTag = "${imageName}:main"
                                echo "Kiểm tra image cục bộ: ${imageTag}"
                                def checkImageCommand = "docker images -q ${imageTag}"
                                def imageId = sh(script: checkImageCommand, returnStdout: true).trim()
                                def imageExists = !imageId.isEmpty()
                                echo "Image ID: ${imageId}"
                                echo "Image exists: ${imageExists}"
                                
                                if (!imageExists) {
                                    echo "Image '${imageTag}' không tồn tại cục bộ. Tiến hành build."
                                    dir(module) {
                                        sh '../mvnw package -DskipTests'
                                        sh "docker build -t ${imageTag} ."
                                    }
                                } else {
                                    echo "Image '${imageTag}' đã tồn tại cục bộ."
                                }
                                echo "Pushing image: ${imageTag}"
                                sh "docker push ${imageTag}"
                            }
                        }
                    }
                }
            }
        }

        // stage('Deploy to Kubernetes') {
        //     steps {
        //         script {
        //             def COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
        //             def modulesList = env.MODULES_CHANGED.split(',')

        //             modulesList.each { module ->
        //                 echo " Deploying ${module} to Kubernetes with image tag: ${COMMIT_ID}"

        //                 // Update image tag in deployment YAML
        //                 sh """
        //                 sed -i 's|image: ${DOCKER_HUB_USERNAME}/${module}:.*|image: ${DOCKER_HUB_USERNAME}/${module}:${COMMIT_ID}|' k8s/${module}/deployment.yaml
        //                 """

        //                 // Apply to Kubernetes
        //                 sh "kubectl apply -f k8s/${module}/deployment.yaml"
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

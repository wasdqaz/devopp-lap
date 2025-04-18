def MODULES_CHANGED = ""
pipeline {
    agent any

    environment {
        DEFAULT_MODULES = "spring-petclinic-admin-server,spring-petclinic-api-gateway,spring-petclinic-config-server,spring-petclinic-customers-service,spring-petclinic-discovery-server,spring-petclinic-genai-service,spring-petclinic-vets-service,spring-petclinic-visits-service"
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10', daysToKeepStr: '7'))
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
        
                        MODULES_CHANGED = changedModules
                        echo "Modules to process: ${env.MODULES_CHANGED}"
                    } 
                    else {
                        echo "No changes detected - stopping pipeline."
                        return
                    }
                }
            }
        }



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

        stage('Build') {
            steps {
                script {
                    def servicesList = MODULES_CHANGED.tokenize(',')

                    if (servicesList.isEmpty()) {
                        echo "No changed services found. Skipping build."
                        return
                    }

                    for (service in servicesList) {
                        echo " Building ${service}..."
                        dir(service) {
                            sh '../mvnw package -DskipTests'
                        }
                    }
                }
            }
        }
        // stage('Cleanup Old Docker Images') {
        //     steps {
        //         script {
        //             def modulesList = env.MODULES_CHANGED.split(',')
                    
        //             withCredentials([usernamePassword(
        //                 credentialsId: 'docker-hub-credentials',
        //                 usernameVariable: 'DOCKERHUB_USER',
        //                 passwordVariable: 'DOCKERHUB_PASSWORD'
        //             )]) {
        //                 // Get Docker Hub token for API calls
        //                 def token = sh(
        //                     script: """
        //                     curl -s -H "Content-Type: application/json" \
        //                     -X POST \
        //                     -d '{"username": "${DOCKERHUB_USER}", "password": "${DOCKERHUB_PASSWORD}"}' \
        //                     https://hub.docker.com/v2/users/login/ | jq -r .token
        //                     """,
        //                     returnStdout: true
        //                 ).trim()
                        
        //                 modulesList.each { module ->
        //                     echo "Checking image count for ${module}..."
                            
        //                     // Get list of tags and count them
        //                     def tagsJson = sh(
        //                         script: """
        //                         curl -s -H "Authorization: JWT ${token}" \
        //                         https://hub.docker.com/v2/repositories/${DOCKERHUB_USER}/${module}/tags?page_size=100
        //                         """,
        //                         returnStdout: true
        //                     ).trim()
                            
        //                     // Parse JSON and get tag names
        //                     def tags = sh(
        //                         script: "echo '${tagsJson}' | jq -r '.results[].name'",
        //                         returnStdout: true
        //                     ).trim().split("\n")
                            
        //                     def tagCount = tags.size()
        //                     echo "Found ${tagCount} tags for ${module}"
                            
        //                     // Only clean up if we have more than 5 tags
        //                     if (tagCount > 5) {
        //                         echo "More than 5 tags found, proceeding with cleanup..."
                                
        //                         // Sort tags (you might need a better sorting strategy depending on your tag format)
        //                         def sortedTags = tags.sort()
        //                         def tagsToKeep = 5
        //                         def tagsToDelete = sortedTags[tagsToKeep..-1]
                                
        //                         echo "Keeping 5 most recent tags, deleting ${tagsToDelete.size()} older tags"
                                
        //                         tagsToDelete.each { tag ->
        //                             sh """
        //                             curl -s -H "Authorization: JWT ${token}" \
        //                             -X DELETE \
        //                             https://hub.docker.com/v2/repositories/${DOCKERHUB_USER}/${module}/tags/${tag}/
        //                             """
        //                             echo "Deleted ${DOCKERHUB_USER}/${module}:${tag}"
        //                         }
        //                     } else {
        //                         echo "Only ${tagCount} tags found for ${module}, cleanup skipped"
        //                     }
        //                 }
        //             }
        //         }
        //     }
        // }
        stage('Build & Push Docker Image') {
            steps {
                script {
                    def COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    def modulesList = MODULES_CHANGED.tokenize(',')
                    if (modulesList.isEmpty()) {
                        echo "No changed services found. Skipping build."
                        return
                    }
                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )]) {
                        sh "docker login -u \${DOCKERHUB_USER} -p \${DOCKERHUB_PASSWORD}"

                        
                        modulesList.each { module ->
                            dir(module) {
                                def imageTag = "${DOCKERHUB_USER}/${module}:${COMMIT_ID}"
                                sh "docker build -t ${imageTag} ."
                                sh "docker push ${imageTag}"
                            }
                        }// module
                    }

                }
            }
        }
        stage('Update Config Repository') {
            steps {
                script {
                    def servicesList = MODULES_CHANGED.tokenize(',')
                    if (servicesList.isEmpty()) {
                        echo "No changed services found. Skipping GitOps update."
                        return
                    }
                    def commitHash = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    
                    // Create a temporary directory for the GitOps repo
                    sh "rm -rf spring-petclinic-microservices-config || true"
                    
                    // Use credentials for Git operations
                    withCredentials([usernamePassword(
                        credentialsId: 'github-credentials', 
                        usernameVariable: 'GIT_USERNAME', 
                        passwordVariable: 'GIT_PASSWORD'
                    )]) {
                        // Clone with credentials
                        
                        sh """
                        echo "Cloning GitOps repository..."
                        echo "https://\${GIT_USERNAME}:\${GIT_PASSWORD}@github.com/wasdqaz/spring-petclinic-microservices-config.git"
                        git clone https://\${GIT_USERNAME}:\${GIT_PASSWORD}@github.com/wasdqaz/spring-petclinic-microservices-config.git
                        """
                        
                        dir('spring-petclinic-microservices-config') {
                            
                            // Update image tags for each changed service
                            for (service in servicesList) {
                                def shortServiceName = service.replaceFirst("spring-petclinic-", "")
                                def valuesFile = "values/dev/values-${shortServiceName}.yaml"
                                
                                // Check if file exists and update with sed
                                sh """
                                if [ -f "${valuesFile}" ]; then
                                    echo "Updating image tag in ${valuesFile}"
                                    sed -i 's/\\(tag:\\s*\\).*/\\1"'${commitHash}'"/' ${valuesFile}
                                else
                                    echo "Warning: ${valuesFile} not found"
                                fi
                                """
                            }
                            
                            // Configure Git and commit changes
                            sh """
                            git config user.email "jenkins@example.com"
                            git config user.name "Jenkins CI"
                            git status
                            
                            # Only commit if there are changes
                            if ! git diff --quiet; then
                                git add .
                                git commit -m "Update image tags for ${MODULES_CHANGED} to ${commitHash}"
                                git push
                                echo "✅ Successfully updated GitOps repository"
                            else
                                echo "ℹ️ No changes to commit in GitOps repository"
                            fi
                            """
                        }
                    }
                    
                    // Clean up after ourselves
                    sh "rm -rf spring-petclinic-microservices-config || true"
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
            echo 'Clean up work space'    
            cleanWs()
        }
        success {
            echo 'Pipeline finished successfully'
        }
        failure {
            echo 'Pipeline failed. Check logs for errors'
        }
    }
}

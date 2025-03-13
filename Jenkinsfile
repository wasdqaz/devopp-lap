pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build and Test') {
            steps {
                def modules = ['vets-service', 'another-service']
                for (module in modules) {
                    def hasChanges = false
                    def changeLogSets = currentBuild.changeSets
                    for (int i = 0; i < changeLogSets.size(); i++) {
                        def entries = changeLogSets[i].items
                        for (int j = 0; j < entries.length; j++) {
                            def entry = entries[j]
                            def files = new ArrayList(entry.affectedFiles)
                            for (int k = 0; k < files.size(); k++) {
                                def file = files[k]
                                if (file.path.startsWith(module)) {
                                    hasChanges = true
                                    break
                                }
                            }
                            if (hasChanges) {
                                break
                            }
                        }
                        if (hasChanges) {
                            break
                        }
                    }
                    if (hasChanges) {
                        dir(module) {
                            sh 'mvn clean package'
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
    }
}

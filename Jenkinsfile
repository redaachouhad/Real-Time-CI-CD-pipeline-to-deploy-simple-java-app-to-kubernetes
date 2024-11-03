pipeline {
    agent any
    tools {
        jdk 'jdk17'
        maven 'maven3'
    }
   environment {
        SCANNER_HOME = tool 'sonarqube-scanner'
        SONAR_PROJECT_KEY = 'TodoList-App-Reda-Achouhad'
        SONAR_PROJECT_NAME = 'TodoList-App-Reda-Achouhad'
        DEPLOY_SERVER = '54.235.62.85'
        DEPLOY_USER = 'ubuntu'
        APP_NAME = 'redachouhad665/todo-list-app'
        EMAIL_FROM = 'ci.cd.pipeline.2024@gmail.com'
        EMAIL_TO = 'luxaryreda@gmail.com'
    }
    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Git Checkout') {
            steps {
               git branch: 'main', changelog: false, poll: false, url: 'https://github.com/redaachouhad/Real-Time-CI-CD-pipeline-to-deploy-simple-java-app-to-kubernetes.git'
            }
        }

        stage('Maven Compile') {
            steps {
                sh "mvn clean compile"
                echo "project compiled successfully"
            }
        }

        stage('Maven Test') {
            steps {
                sh "mvn test"
                echo "tests passed successfully"
            }
        }

        stage('Sonarqube Analysis') {
            steps {
                script{
                    withSonarQubeEnv('sonar') {
                        sh '''
                            $SCANNER_HOME/bin/sonar-scanner \
                                -Dsonar.projectName=$SONAR_PROJECT_NAME \
                                -Dsonar.java.binaries=. \
                                -Dsonar.projectKey=$SONAR_PROJECT_KEY
                        '''
                        echo "sonarqube analysis finished successfully"
                    }
                }
            }
        }


        stage('OWASP Dependency Check') {
            steps {
                dependencyCheck additionalArguments: '--scan target/', odcInstallation: 'owasp'
                dependencyCheckPublisher pattern: 'dependency-check-report.xml'
                echo "owasp check finished successfully"
            }
        }


        stage('Trivy Scan Source Code and Dependencies') {
            steps {
                 sh "trivy fs ."
                 echo "Trivy Scan Source Code and Dependencies finished successfully"
            }
        }

        stage('Maven Package') {
            steps {
                sh "mvn package"
                echo "Maven Package is finished successfully"
            }
        }



        stage("Build Docker Image"){
            steps{
                sh "docker build -t todo-list-app -f Dockerfile ."
                sh "docker tag todo-list-app $APP_NAME:latest"
                echo "docker image buolt successfully"
            }
        }


        stage('Trivy Scan Docker Image') {
            steps {
                // Run Trivy to scan the Docker image and save the report in HTML format
                sh "trivy image --format table -o trivy-image-report.html $APP_NAME:latest"
                echo "docker image scanned successfully"
            }
        }


        stage("Push Docker Image"){
            steps{
                script{
                    withDockerRegistry(credentialsId: 'docker-token', toolName: 'docker') {
                        sh "docker push redachouhad665/todo-list-app:latest"
                        echo "docker image pushed successfully to docker registry"
                    }
                }
            }
        }

        stage("Kubernetes Deployment"){
            steps{
                script{
                    withKubeConfig(caCertificate: '', clusterName: 'kubernetes', contextName: 'kubernetes-admin@kubernetes', credentialsId: 'k8s-token', namespace: 'webapps', restrictKubeConfigAccess: false, serverUrl: 'https://54.175.210.197:6443') {
                       sh "kubectl apply -f k8s_ressources/"
                       echo "application deployed successfully in kubernetes"
                    }
                }
            }
        }



    }

    post {
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed.'
        }
        always {
            script {
                // Define variables for job name, build number, and pipeline status
                def jobName = env.JOB_NAME
                def buildNumber = env.BUILD_NUMBER
                def pipelineStatus = currentBuild.result ?: 'UNKNOWN'
                def bannerColor = (pipelineStatus.toUpperCase() == 'SUCCESS') ? 'green' : 'red'

                // Construct the HTML body for the email notification
                def body = """
                    <html>
                    <body>
                    <div style="border: 4px solid ${bannerColor}; padding: 10px;">
                    <h2>${jobName} - Build ${buildNumber}</h2>
                    <div style="background-color: ${bannerColor}; padding: 10px;">
                    <h3 style="color: white;">Pipeline Status: ${pipelineStatus.toUpperCase()}</h3>
                    </div>
                    <p>Check the <a href="${BUILD_URL}">console output</a>.</p>
                    </div>
                    </body>
                    </html>
                """

                // Send email notification using the Email Extension Plugin
                emailext (
                    subject: "${jobName} - Build ${buildNumber} - ${pipelineStatus.toUpperCase()}",
                    body: body,
                    to: "${EMAIL_TO}", // Recipient email address
                    from: "${EMAIL_FROM}", // Sender email address
                    replyTo: "${EMAIL_FROM}", // Reply-to address
                    mimeType: 'text/html', // Specify MIME type as HTML
                    attachmentsPattern: 'trivy-image-report.html, dependency-check-report.xml' // Attachments if any
                )
            }
        }
    }
}




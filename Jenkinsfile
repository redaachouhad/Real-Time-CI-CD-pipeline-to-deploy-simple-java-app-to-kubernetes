pipeline {
    agent any
    tools {
        jdk 'jdk17'
        maven 'maven3'
    }
   environment {
        SCANNER_HOME = tool 'sonarqube-scanner'
        SONAR_URL = 'http://54.205.244.0:9000/'
        SONAR_PROJECT_KEY = 'TodoList-App'
        SONAR_PROJECT_NAME = 'TodoLis-tApp'
        DEPLOY_SERVER = '98.84.171.184'
        DEPLOY_USER = 'ubuntu'
    }
    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Git Checkout') {
            steps {
               git branch: 'main', changelog: false, poll: false, url: 'https://github.com/redaachouhad/REAL-Time-CI-CD-Pipeline-jenkins-sonarqube-docker-aws.git'
            }
        }

        stage('Maven Compile') {
            steps {
                sh "mvn clean compile"
            }
        }

        stage('Maven Test') {
            steps {
                sh "mvn test"
            }
        }

        stage('Sonarqube Analysis') {
            steps {
                script{
                    withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            $SCANNER_HOME/bin/sonar-scanner \
                                -Dsonar.host.url=$SONAR_URL \
                                -Dsonar.login=$SONAR_TOKEN \
                                -Dsonar.projectName=$SONAR_PROJECT_NAME \
                                -Dsonar.java.binaries=. \
                                -Dsonar.projectKey=$SONAR_PROJECT_KEY
                        '''
                    }
                }

            }
        }

        stage('Maven Package') {
            steps {
                sh "mvn package"
            }
        }


        stage("Build Docker Image"){
            steps{
                sh "docker build -t todo-list-app -f Dockerfile ."
                sh "docker tag todo-list-app redachouhad665/todo-list-app:latest"
            }
        }

        stage("Push Docker Image"){
            steps{
                script{
                    withDockerRegistry(credentialsId: 'docker-token', toolName: 'docker') {
                        sh "docker push redachouhad665/todo-list-app:latest"
                    }
                }
            }
        }

        stage("Deploy Docker Image on EC2") {
            steps {
                script {
                    withCredentials([file(credentialsId: 'ec2-ssh-credentials', variable: 'SSH_KEY_EC2'),
                                     usernamePassword(credentialsId: 'docker-token', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {

                        sh """
                            ssh -o StrictHostKeyChecking=no -i $SSH_KEY_EC2 $DEPLOY_USER@$DEPLOY_SERVER << 'EOF'
                            echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                            docker rm todoListApp --force || true
                            docker rmi redachouhad665/todo-list-app:latest || true
                            docker run -d --name todoListApp -p 8081:8081 redachouhad665/todo-list-app:latest
                            << EOF
                        """
                    }
                }
            }
        }



    }

    post{
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}

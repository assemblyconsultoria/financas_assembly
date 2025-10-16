pipeline {
    agent any

    environment {
        // Docker Registry
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        DOCKER_IMAGE_BACKEND = 'financial-assembly-backend'
        DOCKER_IMAGE_FRONTEND = 'financial-assembly-frontend'

        // Version
        VERSION = "${env.BUILD_NUMBER}"
        GIT_COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()

        // Paths
        BACKEND_DIR = 'backend'
        FRONTEND_DIR = 'frontend'

        // SonarQube
        SONAR_HOST_URL = 'http://sonarqube:9000'
        SONAR_PROJECT_KEY = 'financial-assembly'
    }

    tools {
        maven 'Maven 3.9.6'
        nodejs 'NodeJS 20'
        jdk 'JDK 21'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
                script {
                    sh 'git log -1 --pretty=format:"%h - %an, %ar : %s"'
                }
            }
        }

        stage('Build Backend') {
            steps {
                echo 'Building Spring Boot Backend...'
                dir("${BACKEND_DIR}") {
                    sh 'mvn clean compile -B'
                }
            }
        }

        stage('Unit Tests Backend') {
            steps {
                echo 'Running Backend Unit Tests...'
                dir("${BACKEND_DIR}") {
                    sh 'mvn test -B'
                }
            }
            post {
                always {
                    junit "${BACKEND_DIR}/target/surefire-reports/*.xml"
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: "${BACKEND_DIR}/target/site/jacoco",
                        reportFiles: 'index.html',
                        reportName: 'Backend Code Coverage'
                    ])
                }
            }
        }

        stage('Integration Tests Backend') {
            steps {
                echo 'Running Backend Integration Tests...'
                dir("${BACKEND_DIR}") {
                    sh 'mvn verify -B'
                }
            }
            post {
                always {
                    junit "${BACKEND_DIR}/target/failsafe-reports/*.xml"
                }
            }
        }

        stage('Code Quality Analysis Backend') {
            steps {
                echo 'Running SonarQube Analysis for Backend...'
                dir("${BACKEND_DIR}") {
                    withSonarQubeEnv('SonarQube') {
                        sh '''
                            mvn sonar:sonar \
                              -Dsonar.projectKey=${SONAR_PROJECT_KEY}-backend \
                              -Dsonar.host.url=${SONAR_HOST_URL} \
                              -Dsonar.java.binaries=target/classes
                        '''
                    }
                }
            }
        }

        stage('Quality Gate Backend') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Frontend') {
            steps {
                echo 'Building Angular Frontend...'
                dir("${FRONTEND_DIR}") {
                    sh 'npm ci --legacy-peer-deps'
                    sh 'npm run build:prod'
                }
            }
        }

        stage('Unit Tests Frontend') {
            steps {
                echo 'Running Frontend Unit Tests...'
                dir("${FRONTEND_DIR}") {
                    sh 'npm run test:ci'
                }
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: "${FRONTEND_DIR}/coverage/financial-assembly-frontend",
                        reportFiles: 'index.html',
                        reportName: 'Frontend Code Coverage'
                    ])
                }
            }
        }

        stage('Lint Frontend') {
            steps {
                echo 'Running ESLint for Frontend...'
                dir("${FRONTEND_DIR}") {
                    sh 'npm run lint'
                }
            }
        }

        stage('Security Scan - OWASP Dependency Check') {
            parallel {
                stage('Backend Security Scan') {
                    steps {
                        echo 'Running OWASP Dependency Check for Backend...'
                        dir("${BACKEND_DIR}") {
                            sh '''
                                mvn org.owasp:dependency-check-maven:check \
                                  -DfailBuildOnCVSS=7 \
                                  -DsuppressionFile=owasp-suppressions.xml
                            '''
                        }
                    }
                }
                stage('Frontend Security Scan') {
                    steps {
                        echo 'Running npm audit for Frontend...'
                        dir("${FRONTEND_DIR}") {
                            sh 'npm audit --audit-level=moderate || true'
                        }
                    }
                }
            }
        }

        stage('Package Backend') {
            steps {
                echo 'Packaging Spring Boot Backend...'
                dir("${BACKEND_DIR}") {
                    sh 'mvn package -DskipTests -B'
                }
            }
        }

        stage('Build Docker Images') {
            parallel {
                stage('Build Backend Docker Image') {
                    steps {
                        echo 'Building Backend Docker Image...'
                        script {
                            dir("${BACKEND_DIR}") {
                                def backendImage = docker.build(
                                    "${DOCKER_IMAGE_BACKEND}:${VERSION}",
                                    "--build-arg VERSION=${VERSION} ."
                                )
                                backendImage.tag("${VERSION}")
                                backendImage.tag("${GIT_COMMIT_SHORT}")
                                backendImage.tag("latest")
                            }
                        }
                    }
                }
                stage('Build Frontend Docker Image') {
                    steps {
                        echo 'Building Frontend Docker Image...'
                        script {
                            dir("${FRONTEND_DIR}") {
                                def frontendImage = docker.build(
                                    "${DOCKER_IMAGE_FRONTEND}:${VERSION}",
                                    "--build-arg VERSION=${VERSION} ."
                                )
                                frontendImage.tag("${VERSION}")
                                frontendImage.tag("${GIT_COMMIT_SHORT}")
                                frontendImage.tag("latest")
                            }
                        }
                    }
                }
            }
        }

        stage('Security Scan - Docker Images') {
            parallel {
                stage('Scan Backend Image') {
                    steps {
                        echo 'Scanning Backend Docker Image with Trivy...'
                        sh """
                            trivy image --severity HIGH,CRITICAL \
                              --exit-code 1 \
                              ${DOCKER_IMAGE_BACKEND}:${VERSION}
                        """
                    }
                }
                stage('Scan Frontend Image') {
                    steps {
                        echo 'Scanning Frontend Docker Image with Trivy...'
                        sh """
                            trivy image --severity HIGH,CRITICAL \
                              --exit-code 1 \
                              ${DOCKER_IMAGE_FRONTEND}:${VERSION}
                        """
                    }
                }
            }
        }

        stage('Push Docker Images') {
            when {
                branch 'main'
            }
            steps {
                echo 'Pushing Docker Images to Registry...'
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", "${DOCKER_CREDENTIALS_ID}") {
                        sh """
                            docker push ${DOCKER_IMAGE_BACKEND}:${VERSION}
                            docker push ${DOCKER_IMAGE_BACKEND}:${GIT_COMMIT_SHORT}
                            docker push ${DOCKER_IMAGE_BACKEND}:latest

                            docker push ${DOCKER_IMAGE_FRONTEND}:${VERSION}
                            docker push ${DOCKER_IMAGE_FRONTEND}:${GIT_COMMIT_SHORT}
                            docker push ${DOCKER_IMAGE_FRONTEND}:latest
                        """
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            when {
                branch 'develop'
            }
            steps {
                echo 'Deploying to Staging Environment...'
                script {
                    sh """
                        docker-compose -f docker-compose.staging.yml down
                        docker-compose -f docker-compose.staging.yml up -d
                    """
                }
            }
        }

        stage('Smoke Tests') {
            when {
                branch 'develop'
            }
            steps {
                echo 'Running Smoke Tests...'
                script {
                    sleep(time: 30, unit: 'SECONDS')
                    sh '''
                        curl -f http://localhost:8080/api/v1/actuator/health || exit 1
                        curl -f http://localhost/ || exit 1
                    '''
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                input message: 'Deploy to Production?', ok: 'Deploy'
                echo 'Deploying to Production Environment...'
                script {
                    sh """
                        docker-compose -f docker-compose.prod.yml down
                        docker-compose -f docker-compose.prod.yml up -d
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
            emailext(
                subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """
                    Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' completed successfully.

                    Check console output at ${env.BUILD_URL}
                """,
                to: 'team@financialassembly.com'
            )
        }
        failure {
            echo 'Pipeline failed!'
            emailext(
                subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """
                    Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' failed.

                    Check console output at ${env.BUILD_URL}
                """,
                to: 'team@financialassembly.com'
            )
        }
        unstable {
            echo 'Pipeline is unstable!'
        }
    }
}

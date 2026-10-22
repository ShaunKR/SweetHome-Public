pipeline {
    agent any
    stages {
        stage('Checkout') { steps { checkout scm } }
        stage('Frontend') { steps { dir('frontend') { sh 'npm ci'; sh 'npm run build' } } }
        stage('Backend') { steps { dir('backend') { sh 'sh ./gradlew compileTestJava bootJar --no-daemon' } } }
    }
    post { success { archiveArtifacts artifacts: 'backend/build/libs/*.jar,frontend/dist/**', fingerprint: true } }
}

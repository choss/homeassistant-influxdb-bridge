pipeline {
  agent any
  stages {
    stage('maven compile') {
      steps {
        sh 'bash mvnw clean compile -B'
      }
    }
  }
}
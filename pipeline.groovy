pipelineJob('pipeline') {
  definition {
    cps {
      script(
'''pipeline {
    agent {
        label 'agent'
    }
    tools {
        go 'go-1.20'
    }
    environment {
        GO111MODULE = 'on'
        GOPATH = "/home/jenkins/go"
    }
    stages {
        stage('Setup') {
            steps {
                sh 'mkdir -p $HOME/go/bin'
                sh 'export GOPATH=$HOME/go'
                sh 'export PATH=$PATH:$GOPATH/bin'
                sh 'go install github.com/t-yuki/gocover-cobertura@latest'
            }
        }
        stage('Checkout') {
            steps {
                git 'https://github.com/armando555/go-api-cicd.git'
            }
        }
        stage('Install Dependencies') {
            steps {
                sh 'go mod tidy'
            }
        }
        stage('Build') {
            steps {
                sh 'go build'
            }
        }
        stage('Test') {
            steps {
                sh 'ls'
                sh 'pwd'
                sh 'go test -coverprofile=coverage.txt -covermode count ./'
            }
        }
        stage('Coverage') {
            steps {
                sh '/home/jenkins/go/bin/gocover-cobertura < coverage.txt > coverage.xml'
                cobertura(coberturaReportFile: 'coverage.xml')
            }
        }
        stage('Export Artifacts') {
            steps {
                archiveArtifacts artifacts: 'coverage.xml', fingerprint: true
            }
        }
    }
    post {
        always {
            script {
                withCredentials([string(credentialsId: 'discord-webhook-credential-id', variable: 'DISCORD_WEBHOOK_URL')]) {
                    def discordSendConfig = [
                        description: currentBuild.currentResult == 'SUCCESS' ? "<:LETSFUCKINGOOOOOOOOOOO:809820731134705714> Jenkins Pipeline Build":"<:weynooo:799854983100629022> Jenkins Pipeline Build",
                        footer: JOB_NAME,
                        link: env.BUILD_URL,
                        result: currentBuild.currentResult,
                        title: "Jenkins Pipeline Build: ${JOB_NAME}",
                        webhookURL: DISCORD_WEBHOOK_URL,
                        image: currentBuild.currentResult == 'SUCCESS' ? "https://cdn.discordapp.com/attachments/1081839152942813324/1165799959052951552/undefined_-_Imgur.gif" : "https://cdn.discordapp.com/attachments/1082173364552081449/1165807160236716052/kirbo-mad.gif"
                    ]
                    discordSend(discordSendConfig)
                }
            }
        }
    }
}
''')
      sandbox()
    }
  }
}
pipelineJob('pipeline') {
  definition {
    cps {
      script(
'''pipeline {
    agent {
        label 'agent'
    }
    tools {
        dockerTool 'docker'
    }
    environment {
        GO111MODULE = 'on'
        GOPATH = "/home/jenkins/go"
    }
    stages {
        stage('Setup') {
            steps {
                sh 'docker ps'
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
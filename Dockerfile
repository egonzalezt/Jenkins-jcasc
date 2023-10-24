FROM jenkins/jenkins:lts-jdk17
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
COPY pipeline.groovy /var/jenkins_home/pipeline.groovy
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt
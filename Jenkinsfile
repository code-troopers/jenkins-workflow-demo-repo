#!groovy
stage 'build'
    def m2Repo = '-v /var/jenkins_home/.m2:/home/jenkins/.m2'
    def timezone = '-e TZ=Europe/Paris'
    docker.image("codetroopers/jenkins-slave-jdk8-restx").inside("${m2Repo} ${timezone}"){
        checkout scm
        sh "MAVEN_OPTS=-Dfile.encoding=UTF-8 mvn clean install -B"
        step([$class: 'ArtifactArchiver', artifacts: 'srv/target/dependency/webapp-runner.jar, srv/target/*.war, run.sh, Dockerfile'])
        step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
        stash includes: 'run.sh,srv/target/dependency/webapp-runner.jar,srv/target/*.war,Dockerfile', name: 'dockerBuild'
    }
    
stage 'dockerbuild'
node{
  ws{
    unstash 'dockerBuild'
    docker.build("codetroopers/jenkins-workflow-demo:${env.BUILD_ID}")
  }
}

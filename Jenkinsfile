#!groovy
stage 'build'
    docker.image("codetroopers/jenkins-slave-jdk8-restx").inside('-v /var/jenkins_home/.m2:/home/jenkins/.m2'){
        checkout scm
        sh "MAVEN_OPTS=-Dfile.encoding=UTF-8 mvn clean install -B -Ppackage"
        step([$class: 'ArtifactArchiver', artifacts: 'srv/target/dependency/webapp-runner.jar, srv/target/*.war, run.sh, Dockerfile', fingerprint: true])
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
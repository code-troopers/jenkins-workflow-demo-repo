FROM java:8

MAINTAINER Cedric Gatay <c.gatay@code-troopers.com>

ENV PORT 8080

EXPOSE 8080
WORKDIR /app

ADD run.sh /app/run.sh
ADD srv/target/dependency/newrelic /app/srv/target/dependency/newrelic
ADD srv/target/dependency/webapp-runner.jar /app/srv/target/dependency/
ADD srv/target/*.war /app/srv/target/

CMD ["/app/run.sh"]
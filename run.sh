#!/bin/sh
exec java \
    -Drestx.http.XForwardedSupport=all \
    -Dfile.encoding=UTF-8 \
    -jar srv/target/dependency/webapp-runner.jar --port 8080 srv/target/*.war

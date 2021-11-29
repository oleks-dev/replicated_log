FROM openjdk:11.0.8-slim

EXPOSE 8080

ENV WORKDIR="/opt/" \
    APP_JAR="master_node-*.jar" \
    JAVA_OPTS=""

COPY build/libs/$APP_JAR $WORKDIR
COPY init.sh $WORKDIR

WORKDIR $WORKDIR
ENTRYPOINT ["./init.sh"]
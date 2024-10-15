FROM --platform=$TARGETPLATFORM azul/zulu-openjdk-alpine:21-jre-headless

RUN mkdir /app
WORKDIR /app

COPY build/libs/*-all.jar /app/marathon.jar

ENTRYPOINT ["java"]
CMD ["-jar", "/app/marathon.jar"]

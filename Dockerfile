FROM --platform=$TARGETPLATFORM azul/zulu-openjdk:21.0.6-jre

RUN mkdir /app
WORKDIR /app

COPY build/libs/*-all.jar /app/marathon.jar

ENTRYPOINT ["java"]
CMD ["-jar", "/app/marathon.jar"]

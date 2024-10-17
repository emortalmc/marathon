FROM --platform=$TARGETPLATFORM azul/zulu-openjdk:21.0.5-jre

RUN mkdir /app
WORKDIR /app

# Download packages
RUN apt-get update && apt-get install -y wget

COPY build/libs/*-all.jar /app/marathon.jar

ENTRYPOINT ["java"]
CMD ["-jar", "/app/marathon.jar"]

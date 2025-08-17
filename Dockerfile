#FROM ghcr.io/graalvm/native-image-community:24
#
#RUN microdnf install tar
#
#WORKDIR /app
#
#COPY build/libs/*-all.jar /app/marathon.jar
#
#ENTRYPOINT ["java"]
#CMD ["-jar", "/app/marathon.jar"]
FROM debian:trixie-slim

RUN mkdir /app
WORKDIR /app

COPY build/native/nativeCompile/marathon /app/marathon

CMD ["/app/marathon"]
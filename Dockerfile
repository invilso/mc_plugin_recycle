# syntax=docker/dockerfile:1.7

FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml ./
COPY src ./src
RUN mvn -B --no-transfer-progress clean package

FROM eclipse-temurin:21-jre AS runner
ARG JAR_PATH=recycler.jar
WORKDIR /opt/recycler
COPY --from=builder /build/target/recycler-*.jar ${JAR_PATH}
CMD ["/bin/sh", "-c", "ls -al && echo 'Recycler plugin jar is available under /opt/recycler' && sleep infinity"]

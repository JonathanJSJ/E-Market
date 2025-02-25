FROM maven:3.9.9-eclipse-temurin-21-jammy as build-before
LABEL authors="vinycius;gabriel"
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ADD . /usr/src/app
RUN mvn package

FROM eclipse-temurin:21-jdk-noble
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY --from=build-before /usr/src/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

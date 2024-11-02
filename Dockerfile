FROM openjdk:17-jdk-slim
WORKDIR /usr/app
COPY /target/*.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
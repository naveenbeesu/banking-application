FROM openjdk:17
LABEL authors="nbeesu"
EXPOSE 8080
COPY target/banking-application.jar banking-application.jar

ENTRYPOINT ["java", "-jar", "/banking-application.jar"]
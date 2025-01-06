FROM openjdk:21
WORKDIR /app
COPY target/Backend-0.0.1-SNAPSHOT.jar /app/Backend-0.0.1-SNAPSHOT.jar
EXPOSE 9001
ENTRYPOINT ["java", "-jar", "Backend-0.0.1-SNAPSHOT.jar"]
FROM openjdk:11

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} account-0.0.1-SNAPSHOT.jar

EXPOSE 50055

ENTRYPOINT ["java","-jar","/account-0.0.1-SNAPSHOT.jar"]
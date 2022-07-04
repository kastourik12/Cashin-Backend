FROM openjdk:18-alpine
ADD target/CashIn-0.0.1-SNAPSHOT.jar.jar CashIn-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar","CashIn-0.0.1-SNAPSHOT.jar"]
EXPOSE 8123
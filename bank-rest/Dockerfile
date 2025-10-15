FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/bank-rest-0.0.1-SNAPSHOT.jar ./bank-rest.jar
CMD ["java", "-jar", "bank-rest.jar"]

FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && addgroup --system spring && adduser --system --ingroup spring spring

COPY --from=build /app/target/spring_core-1.0-SNAPSHOT.jar app.jar
RUN chown spring:spring app.jar

USER spring:spring

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
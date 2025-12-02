FROM eclipse-temurin:21-jdk as builder
WORKDIR /app

COPY news/pom.xml ./news/
COPY news/src ./news/src/

RUN apt-get update && apt-get install -y maven
RUN mvn -f news/pom.xml -DskipTests clean package

FROM eclipse-termurin:21-jdk
WORKDIR /app

COPY --from=builder /app/news/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]


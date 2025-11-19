FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY news/target/reddit-nitter-news-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]


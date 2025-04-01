FROM openjdk:23-jdk-slim
WORKDIR /app
COPY build/libs/gift-bot-0.0.1.jar .
EXPOSE 8080
CMD java -jar /app/gift-bot-0.0.1.jar

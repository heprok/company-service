FROM gradle:jdk11-openj9 as builder

WORKDIR /app
COPY . .

RUN gradle bootJar --no-daemon

FROM arm64v8/openjdk:11-jdk-slim

COPY --from=builder /api/build/libs/*.jar /application.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","application.jar"]

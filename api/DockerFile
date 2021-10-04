FROM arm64v8/gradle:jdk11-openj9 as builder

WORKDIR /app

ARG CI_DEPLOY_PASSWORD
ENV CI_DEPLOY_PASSWORD="${CI_DEPLOY_PASSWORD}"

COPY . .

RUN gradle api:bootJar --no-daemon

FROM arm64v8/openjdk:11-jdk-slim

COPY --from=builder /app/api/build/libs/*.jar /application.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","application.jar"]

FROM arm64v8/gradle:jdk11-openj9 as builder

WORKDIR /app
COPY . .

ARG CI_DEPLOY_PASSWORD
ENV CI_DEPLOY_PASSWORD="${CI_DEPLOY_PASSWORD}"

RUN gradle updater:bootJar --no-daemon

FROM arm64v8/openjdk:11-jdk-slim

COPY --from=builder /app/updater/build/libs/*.jar /application.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","application.jar"]

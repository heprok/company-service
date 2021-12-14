FROM --platform=linux/arm64/v8 public.ecr.aws/docker/library/gradle:jdk11-hotspot as builder

WORKDIR /app
COPY . .

ARG CI_DEPLOY_PASSWORD
ENV CI_DEPLOY_PASSWORD="${CI_DEPLOY_PASSWORD}"

RUN gradle updater:bootJar --no-daemon

FROM --platform=linux/arm64/v8 public.ecr.aws/docker/library/openjdk:11-jdk-slim

COPY --from=builder /app/updater/build/libs/*.jar /application.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","application.jar"]

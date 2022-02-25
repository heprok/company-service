import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version Versions.SPRING_BOOT apply false
    id("io.spring.dependency-management") version Versions.SPRING_DEPENDENCY_MANAGEMENT apply false
    id("com.diffplug.spotless") version Versions.SPOTLESS apply false
    id("com.netflix.dgs.codegen") version Versions.DGS_CODEGEN apply false
    kotlin("jvm") version Versions.KOTLIN apply false
    kotlin("kapt") version Versions.KOTLIN apply false
    kotlin("plugin.spring") version Versions.KOTLIN apply false
    kotlin("plugin.jpa") version Versions.KOTLIN apply false
    kotlin("plugin.allopen") version Versions.KOTLIN apply false
}

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

allprojects {
    group = "com.briolink"
    version = "1.0.0-SNAPSHOT"

    tasks.withType<JavaCompile> {
        sourceCompatibility = Versions.JAVA
        targetCompatibility = Versions.JAVA
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = Versions.JAVA
        }
    }
}

subprojects {
    repositories {
        println(System.getenv("SSH_AUTH_SOCK"))
        mavenCentral()
        mavenLocal()
        setOf(
            29889174, // Briolink Event lib
            33422039, // Briolink Location lib
            33688770, // Briolink Sync lib
        ).forEach {
            maven {
                url = uri("https://gitlab.com/api/v4/projects/$it/packages/maven")
                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
                credentials(HttpHeaderCredentials::class) {
                    name = "Deploy-Token"
                    value = System.getenv("CI_DEPLOY_PASSWORD")
                }
            }
        }
    }

    apply {
        plugin("kotlin")
        plugin("io.spring.dependency-management")
    }

    val implementation by configurations

    dependencies {
        // Briolnik Event
        implementation("com.briolink:event:${Versions.BRIOLINK_EVENT}")
        implementation("me.paulschwarz:spring-dotenv:${Versions.SPRING_DOTENV}")
        implementation("com.briolink.lib:location:${Versions.BRIOLINK_LOCATION}")
        implementation("com.briolink.lib:sync:${Versions.BRIOLINK_SYNC}")
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

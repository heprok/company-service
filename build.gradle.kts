import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.21"
    val springBootVersion = "2.5.4"
    val springDependencyManagementVersion = "1.0.11.RELEASE"
    val spotlessVersion = "5.14.1"
    val dgsCodegen = "5.0.6"

    id("org.springframework.boot") version springBootVersion apply false
    id("io.spring.dependency-management") version springDependencyManagementVersion apply false
    id("com.diffplug.spotless") version spotlessVersion apply false
    id("com.netflix.dgs.codegen") version dgsCodegen apply false
    kotlin("jvm") version kotlinVersion apply false
    kotlin("kapt") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("plugin.jpa") version kotlinVersion apply false
    kotlin("plugin.allopen") version kotlinVersion apply false
}

buildscript {
    repositories {
        mavenCentral()
    }
}

allprojects {
    group = "com.briolink"
    version = "1.0.0-SNAPSHOT"

    extra["springCloudVersion"] = "2.3.2"

    val javaVersion = "11"

    tasks.withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = javaVersion
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
    }

    apply {
        plugin("io.spring.dependency-management")
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

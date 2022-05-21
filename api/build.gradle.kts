plugins {
    application

    id("org.springframework.boot")
    id("com.netflix.dgs.codegen")

    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
}

dependencies {
    // Project
    implementation(project(":common"))
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-autoconfigure-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // FasterXML
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // FasterXML
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))

    // Liquibase
    implementation("org.liquibase:liquibase-core:${Versions.LIQUIBASE_CORE}")

    // PostgreSQL JDBC Driver
    runtimeOnly("org.postgresql:postgresql")

    // Netflix DGS
    implementation(
        platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:latest.release"),
    )
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")

    // kotlin-logging
    implementation("io.github.microutils:kotlin-logging-jvm:${Versions.KOTLIN_LOGGING_JVM}")

    // CSV
    implementation("com.opencsv:opencsv:${Versions.OPEN_CSV}")

    // MapStruct
    implementation("org.mapstruct:mapstruct:${Versions.MAPSTRUCT}")
    kapt("org.mapstruct:mapstruct-processor:${Versions.MAPSTRUCT}")
}

dependencyManagement {
    imports {
        mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:${Versions.AWSPRING_CLOUD}")
    }
}

kapt {
    arguments {
        arg("mapstruct.verbose", "true")
    }
}

java.sourceSets["main"].java {
    srcDir("${project.buildDir.absolutePath}/dgs-codegen/generated")
}

tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
    packageName = "com.briolink.companyservice.api"
    language = "kotlin"
    typeMapping = mutableMapOf(
        "Url" to "java.net.URL",
        "Upload" to "org.springframework.web.multipart.MultipartFile",
        "Year" to "java.time.Year",
    )
    generatedSourcesDir = "${project.buildDir.absolutePath}/dgs-codegen"
}

tasks.compileJava {
    dependsOn(tasks.processResources)
}

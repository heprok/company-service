plugins {
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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.5.4")
    implementation("org.springframework.boot:spring-boot-starter-validation:2.5.4")
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.4")
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.5.4")
    implementation("org.springframework.boot:spring-boot-starter-security:2.5.4")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:2.5.4")
    developmentOnly("org.springframework.boot:spring-boot-devtools:2.5.4")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.5.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.4")

//    // Spring Cloud
//    implementation("io.awspring.cloud:spring-cloud-starter-aws-messaging:2.3.2")
//    implementation("io.awspring.cloud:spring-cloud-aws-autoconfigure:2.3.2")

    // FasterXML
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")

    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))

    // Liquibase
    implementation("org.liquibase:liquibase-core:4.4.3")

    // MariaDB JDBC Driver
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:2.7.3")

    // Netflix DGS
    implementation(
        platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:latest.release")
    )
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter:4.8.0")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars:4.8.0")

    // kotlin-logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.4.2.Final")
    kapt("org.mapstruct:mapstruct-processor:1.4.2.Final")

    // Konform
    implementation("io.konform:konform:0.3.0")
}

//dependencyManagement {
//    imports {
//        mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:${property("springCloudVersion")}")
//    }
//}

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
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
        "Upload" to "org.springframework.web.multipart.MultipartFile"
    )
    generatedSourcesDir = "${project.buildDir.absolutePath}/dgs-codegen"
}

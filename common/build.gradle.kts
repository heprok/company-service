plugins {
    id("org.springframework.boot")
//    id("com.ewerk.gradle.plugins.querydsl") version "1.0.10"
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
    }
allOpen {
    annotations("javax.persistence.Entity", "javax.persistence.MappedSuperclass", "javax.persistence.Embedabble")
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-autoconfigure-processor:${Versions.SPRING_BOOT}")
    kapt("org.springframework.boot:spring-boot-configuration-processor:${Versions.SPRING_BOOT}")


    // FasterXML
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))

    // MapStruct
    implementation("org.mapstruct:mapstruct:${Versions.MAPSTRUCT}")
    kapt("org.mapstruct:mapstruct-processor:${Versions.MAPSTRUCT}")

    //FasterXML
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON_MODULE}")
//
//    implementation("com.querydsl:querydsl-jpa:${Versions.QUERY_DSL}")
//    implementation("com.querydsl:querydsl-apt:${Versions.QUERY_DSL}")
//    compile("com.querydsl:querydsl-core:${Versions.QUERY_DSL}")
//    compile("com.querydsl:querydsl-jpa:${Versions.QUERY_DSL}")
//    kapt("com.querydsl:querydsl-apt:${Versions.QUERY_DSL}:jpa")

    //querydsl
//    compile("com.querydsl:querydsl-jpa:${Versions.QUERY_DSL}")
//    kapt("com.querydsl:querydsl-apt:${Versions.QUERY_DSL}:jpa")
//    // JSR-330 javax.inject annotations - required by querydsl
//
//    annotationProcessor(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa" )

    kapt("org.hibernate:hibernate-jpamodelgen:5.4.30.Final")

    // Hibernate Types 55
    implementation("com.vladmihalcea:hibernate-types-55:${Versions.HIBERNATE_TYPES_55}")

    // IBM ICU4J
    implementation("com.ibm.icu:icu4j:${Versions.IBM_ICU4J}")
}
//

//java.sourceSets["main"].java {
//    srcDir("${project.buildDir.absolutePath}/querydsl/generated")
//}
//tasks.withType<com.ewerk.gradle.plugins.tasks.InitQuerydslSourcesDir> {
//    buildDir = file("${project.buildDir.absolutePath}/querydsl")
//
//}
//tasks.withType<com.ewerk.gradle.plugins.tasks.QuerydslCompile> {
//
//    buildDir = file("${project.buildDir.absolutePath}/querydsl")
//}
//querydsl {
//    jpa = true
//    querydslSourcesDir = "${project.buildDir.absolutePath}/querydsl/main"
//}

kapt {
    arguments {
        arg("mapstruct.verbose", "true")
    }
}

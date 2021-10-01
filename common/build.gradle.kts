plugins {
    id("org.springframework.boot")

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

    // FasterXML
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))

//    //mapstruct
//    implementation("org.mapstruct:mapstruct:")
//    annotationProcessor("org.mapstruct:mapstruct-processor:1.4.2.Final")

    //
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON_MODULE}")

    // Hibernate Types 55
    implementation("com.vladmihalcea:hibernate-types-55:${Versions.HIBERNATE_TYPES_55}")

    // Querydsl
    api("com.querydsl:querydsl-jpa")
    api("com.querydsl:querydsl-core")
    kapt("com.querydsl", name = "querydsl-apt", classifier = "jpa")

    // IBM ICU4J
    implementation("com.ibm.icu:icu4j:${Versions.IBM_ICU4J}")
}

kapt {
    annotationProcessor("com.querydsl.apt.jpa.JPAAnnotationProcessor")
}

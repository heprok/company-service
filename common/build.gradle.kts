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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.5.4")

    // FasterXML
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")

    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))

    // Hibernate Types 55
    implementation("com.vladmihalcea:hibernate-types-55:2.12.1")

    // IBM icu
    implementation("com.ibm.icu:icu4j:69.1")

    // Querydsl
    api("com.querydsl:querydsl-jpa:5.0.0")
    api("com.querydsl:querydsl-core:5.0.0")
    kapt("com.querydsl", name = "querydsl-apt", classifier = "jpa")
}

kapt {
    annotationProcessor("com.querydsl.apt.jpa.JPAAnnotationProcessor")
}

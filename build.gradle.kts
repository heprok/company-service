
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version Versions.SPRING_BOOT apply false
    id("io.spring.dependency-management") version Versions.SPRING_DEPENDENCY_MANAGEMENT apply false
    id("com.diffplug.spotless") version Versions.SPOTLESS
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
    apply {
        plugin("com.diffplug.spotless")
    }

    repositories {
        mavenCentral()
    }

    group = "com.briolink"
    version = "1.2-SNAPSHOT"

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

    spotless {
        kotlin {
            target("**/*.kt")

            // https://github.com/diffplug/spotless/issues/142
            ktlint().userData(
                mapOf(
                    "indent_style" to "space",
                    "max_line_length" to "140",
                    "indent_size" to "4",
                    "ij_kotlin_code_style_defaults" to "KOTLIN_OFFICIAL",
                    "ij_kotlin_line_comment_at_first_column" to "false",
                    "ij_kotlin_line_comment_add_space" to "true",
                    "ij_kotlin_name_count_to_use_star_import" to "2147483647",
                    "ij_kotlin_name_count_to_use_star_import_for_members" to "2147483647",
                    "ij_kotlin_keep_blank_lines_in_declarations" to "1",
                    "ij_kotlin_keep_blank_lines_in_code" to "1",
                    "ij_kotlin_keep_blank_lines_before_right_brace" to "0",
                    "ij_kotlin_align_multiline_parameters" to "false",
                    "ij_continuation_indent_size" to "4",
                    "insert_final_newline" to "true",
                ),
            )

            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }

        kotlinGradle {
            target("**/*.gradle.kts", "*.gradle.kts")

            ktlint().userData(mapOf("indent_size" to "4", "continuation_indent_size" to "4"))

            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
    }

    tasks.withType<KotlinCompile> {
        dependsOn("spotlessApply")
        dependsOn("spotlessCheck")
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        setOf(
            29889174, // BL Event
            33422039, // BL Location
            33688770, // BL Sync
            32844103, // BL Permission
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
        implementation("me.paulschwarz:spring-dotenv:${Versions.SPRING_DOTENV}")
        implementation("com.briolink.lib:event:${Versions.BRIOLINK_EVENT}")
        implementation("com.briolink.lib:common:${Versions.BRIOLINK_COMMON}")
        implementation("com.briolink.lib:permission:${Versions.BRIOLINK_PERMISSION}")
        implementation("com.briolink.lib:location:${Versions.BRIOLINK_LOCATION}")
        implementation("com.briolink.lib:sync:${Versions.BRIOLINK_SYNC}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

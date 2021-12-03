package com.briolink.companyservice.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(
    basePackages = [
        "com.briolink.companyservice.common.jpa.read.entity",
        "com.briolink.companyservice.common.jpa.write.entity",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.briolink.companyservice.common.jpa.read.repository",
        "com.briolink.companyservice.common.jpa.write.repository",
    ],
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

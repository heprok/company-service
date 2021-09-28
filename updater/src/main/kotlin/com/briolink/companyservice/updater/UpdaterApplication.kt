package com.briolink.companyservice.updater

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(
    basePackages = [
        "com.briolink.companyservice.common.jpa.read.entity",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.briolink.companyservice.common.jpa.read.repository",
    ],
)
class UpdaterApplication

fun main(args: Array<String>) {
    runApplication<UpdaterApplication>(*args)
}

package com.briolink.companyservice.updater

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
        scanBasePackages = [
            "com.briolink.companyservice.updater",
            "com.briolink.companyservice.common.service",
            "com.briolink.companyservice.common.config",
            "com.briolink.companyservice.common.jpa.location",
            "com.briolink.companyservice.common.jpa.enumration",
        ],
)
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

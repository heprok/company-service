package com.briolink.companyservice.common.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.briolink.companyservice.common.config"
    ]
)
class AutoConfiguration

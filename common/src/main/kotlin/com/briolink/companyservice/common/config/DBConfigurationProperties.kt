package com.briolink.companyservice.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("app.db")
@Suppress("ConfigurationProperties")
data class DBConfigurationProperties(
    val schemaPrefix: String? = null,
)

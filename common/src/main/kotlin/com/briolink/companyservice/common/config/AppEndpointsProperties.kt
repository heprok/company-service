package com.briolink.companyservice.common.config

import com.sun.istack.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.app-endpoints")
class AppEndpointsProperties {
    @NotNull
    lateinit var companyservice: String

    @NotNull
    lateinit var connection: String

    @NotNull
    lateinit var expverification: String

    @NotNull
    lateinit var user: String
}

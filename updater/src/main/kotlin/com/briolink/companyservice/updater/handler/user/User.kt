package com.briolink.companyservice.updater.handler.user

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.UUID

data class User(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    var slug: String,
    @JsonProperty
    val firstName: String,
    @JsonProperty
    val lastName: String,
    @JsonProperty
    val description: String? = null,
    @JsonProperty
    val location: String? = null,
    @JsonProperty
    val image: URL? = null,
    @JsonProperty
    val twitter: String? = null,
    @JsonProperty
    val facebook: String? = null,
)

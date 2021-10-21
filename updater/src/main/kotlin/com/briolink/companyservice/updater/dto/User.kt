package com.briolink.companyservice.updater.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.*

data class User(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("slug")
    var slug: String,
    @JsonProperty("firstName")
    val firstName: String,
    @JsonProperty("lastName")
    val lastName: String,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("location")
    val location: String? = null,
    @JsonProperty("image")
    val image: URL? = null,
    @JsonProperty("twitter")
    val twitter: String? = null,
    @JsonProperty("facebook")
    val facebook: String? = null,
)

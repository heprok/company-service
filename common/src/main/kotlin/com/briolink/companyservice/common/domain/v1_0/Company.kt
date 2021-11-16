package com.briolink.companyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.*

data class Company(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String,
    @JsonProperty
    val slug: String,
    @JsonProperty
    val website: URL? = null,
    @JsonProperty
    val logo: URL? = null,
    @JsonProperty
    val description: String? = null,
    @JsonProperty
    val isTypePublic: Boolean = true,
    @JsonProperty
    val location: String? = null,
    @JsonProperty
    val country: String? = null,
    @JsonProperty
    val state: String? = null,
    @JsonProperty
    val city: String? = null,
    @JsonProperty
    val facebook: String? = null,
    @JsonProperty
    val twitter: String? = null,
    @JsonProperty
    val occupation: Occupation? = null,
    @JsonProperty
    val industry: Industry? = null,
    @JsonProperty
    val createdBy: UUID,
    @JsonProperty
    val keywords: ArrayList<Keyword>? = ArrayList()
) : Domain {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Keyword(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val name: String,
    )
}

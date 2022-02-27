package com.briolink.companyservice.common.domain.v1_0

import com.briolink.lib.location.model.LocationId
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.UUID

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
    val shortDescription: String? = null,
    @JsonProperty
    val isTypePublic: Boolean = true,
    @JsonProperty
    val locationId: LocationId? = null,
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
    data class Keyword(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val name: String,
    )
}

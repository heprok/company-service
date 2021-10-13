package com.briolink.companyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.*

data class Company(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("slug")
    val slug: String,
    @JsonProperty("website")
    val website: String,
    @JsonProperty("logo")
    val logo: URL? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("isTypePublic")
    val isTypePublic: Boolean? = null,
    @JsonProperty("location")
    val location: String? = null,
//    @JsonProperty("country")
//    val country: String? = null,
//    @JsonProperty("state")
//    val state: String? = null,
//    @JsonProperty("city")
//    val city: String? = null,
    @JsonProperty("facebook")
    val facebook: String? = null,
    @JsonProperty("twitter")
    val twitter: String? = null,
    @JsonProperty("occupation")
    val occupation: Occupation? = null,
    @JsonProperty("industry")
    val industry: Industry? = null,
    @JsonProperty("statistic")
    val statistic: Statistic? = null,
    @JsonProperty("keywords")
    val keywords: ArrayList<Keyword>? = ArrayList()
) : Domain {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Keyword(
        @JsonProperty("id")
        val id: UUID,
        @JsonProperty("name")
        val name: String,
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Statistic(
        @JsonProperty("serviceProvidedCount")
        val serviceProvidedCount: Int = 0,
        @JsonProperty("collaboratingCompanyCount")
        val collaboratingCompanyCount: Int = 0,
        @JsonProperty("collaboratingPeopleCount")
        val collaboratingPeopleCount: Int = 0,
        @JsonProperty("totalConnectionCount")
        val totalConnectionCount: Int = 0
    )
}

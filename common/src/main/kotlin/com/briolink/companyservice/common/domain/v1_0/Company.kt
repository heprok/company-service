package com.briolink.companyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonProperty

data class Company(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("slug")
    val slug: String,
    @JsonProperty("website")
    val website: String,
    @JsonProperty("logo")
    val logo: String? = null,
    @JsonProperty("about")
    val about: String? = null,
    @JsonProperty("isTypePublic")
    val isTypePublic: Boolean = true,
    @JsonProperty("country")
    val country: String? = null,
    @JsonProperty("state")
    val state: String? = null,
    @JsonProperty("city")
    val city: String? = null,
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
    val keywords: List<Keyword?>? = null
) : Domain

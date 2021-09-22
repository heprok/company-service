package com.briolink.companyservice.common.domain.v1

import com.fasterxml.jackson.annotation.JsonProperty

data class Company(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("website")
    val website: String,
    @JsonProperty("logo")
    val logo: String? = null,
    @JsonProperty("about")
    val about: String? = null,
    @JsonProperty("country")
    val country: String? = null,
    @JsonProperty("state")
    val state: String? = null,
    @JsonProperty("city")
    val city: String? = null,
    @JsonProperty("industry")
    val industry: com.briolink.companyservice.common.domain.v1.Industry? = null,
    @JsonProperty("keyWords")
    val keyWords: MutableList<com.briolink.companyservice.common.domain.v1.KeyWord> = mutableListOf()
) : com.briolink.companyservice.common.domain.v1.Domain

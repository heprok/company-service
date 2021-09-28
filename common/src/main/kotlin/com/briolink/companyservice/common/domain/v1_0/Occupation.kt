package com.briolink.companyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonProperty

data class Occupation(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String
) : Domain

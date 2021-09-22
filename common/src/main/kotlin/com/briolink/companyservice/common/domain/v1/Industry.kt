package com.briolink.companyservice.common.domain.v1

import com.fasterxml.jackson.annotation.JsonProperty

data class Industry(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
) : com.briolink.companyservice.common.domain.v1.Domain

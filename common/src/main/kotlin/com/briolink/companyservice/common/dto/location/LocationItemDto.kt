package com.briolink.companyservice.common.dto.location

import com.fasterxml.jackson.annotation.JsonProperty

data class LocationItemDto(
    @JsonProperty
    val id: String,
    @JsonProperty
    val name: String,
)

package com.briolink.companyservice.common.dto.location

import com.fasterxml.jackson.annotation.JsonProperty

data class LocationInfoDto(
    @JsonProperty
    val country: LocationInfoType,
    @JsonProperty
    val state: LocationInfoType? = null,
    @JsonProperty
    val city: LocationInfoType? = null,
) {
    override fun toString(): String =
        if (city != null) "${city.name}, ${state!!.name}, ${country.name}"
        else if (state != null) "${state.name}, ${country.name}"
        else country.name
}

data class LocationInfoType(
    @JsonProperty
    val id: Int,
    @JsonProperty
    val name: String,
)

data class LocationItemDto(
    @JsonProperty
    val id: String,
    @JsonProperty
    val name: String,
)

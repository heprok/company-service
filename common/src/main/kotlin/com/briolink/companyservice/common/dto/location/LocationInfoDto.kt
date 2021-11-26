package com.briolink.companyservice.common.dto.location

import com.fasterxml.jackson.annotation.JsonProperty

data class LocationInfoDto(
    @JsonProperty
    val country: CountryDto,
    @JsonProperty
    val state: StateDto? = null,
    @JsonProperty
    val city: CityDto? = null,
    @JsonProperty
    val location: String
)

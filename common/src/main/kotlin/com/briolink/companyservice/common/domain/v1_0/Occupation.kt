package com.briolink.companyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Occupation(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("name")
    val name: String
) : Domain

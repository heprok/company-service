package com.briolink.companyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Keyword(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String
) : Domain

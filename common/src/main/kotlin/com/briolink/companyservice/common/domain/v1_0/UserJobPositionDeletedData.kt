package com.briolink.companyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class UserJobPositionDeletedData(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("userId")
    val userId: UUID,
    @JsonProperty("isCurrent")
    val isCurrent: Boolean = false
) : Domain


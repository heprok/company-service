package com.briolink.companyservice.updater.handler.userjobposition

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class UserJobPositionDeletedData(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("userId")
    val userId: UUID,
    @JsonProperty("isCurrent")
    val isCurrent: Boolean = false
)

package com.briolink.companyservice.updater.handler.userjobposition

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class UserJobPositionDeletedData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val userId: UUID,
    @JsonProperty
    val isCurrent: Boolean = false
)

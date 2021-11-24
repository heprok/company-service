package com.briolink.companyservice.updater.handler.userjobposition

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.UUID

data class UserJobPosition(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val title: String,
    @JsonProperty
    val startDate: LocalDate? = null,
    @JsonProperty
    val endDate: LocalDate? = null,
    @JsonProperty
    val isCurrent: Boolean = false,
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val userId: UUID,
)

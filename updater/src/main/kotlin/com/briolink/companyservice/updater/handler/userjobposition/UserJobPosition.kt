package com.briolink.companyservice.updater.handler.userjobposition

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.*

data class UserJobPosition(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("startDate")
    val startDate: LocalDate? = null,
    @JsonProperty("endDate")
    val endDate: LocalDate? = null,
    @JsonProperty("isCurrent")
    val isCurrent: Boolean = false,
    @JsonProperty("companyId")
    val companyId: UUID,
    @JsonProperty("userId")
    val userId: UUID,
)


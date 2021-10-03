package com.briolink.companyservice.common.domain.v1_0

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
    @JsonProperty("company")
    val companyId: UUID,
    @JsonProperty("userId")
    val userId: UUID,
) : Domain


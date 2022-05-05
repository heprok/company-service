package com.briolink.companyservice.common.jpa.read.entity.statistic

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.UUID

interface ChartListItem {
    val companyId: UUID
}

class ChartDataList<T>(
    @JsonProperty
    var name: String,
    @JsonProperty
    var items: MutableList<T> = mutableListOf(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
class ChartList<T> {
    @JsonProperty
    var data: MutableMap<String, ChartDataList<T>> = mutableMapOf()
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartListItemWithMarketSegmentAndLocation(
    @JsonProperty override val companyId: UUID,
    @JsonProperty var verifiedProjects: Int,
) : ChartListItem

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartListItemWithRoleAndProject(
    @JsonProperty override val companyId: UUID,
    @JsonProperty val role: String,
    @JsonProperty val serviceName: String,
    @JsonProperty val startDate: LocalDate,
    @JsonProperty val endDate: LocalDate?,
) : ChartListItem

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartListItemWithVerifyUsed(
    @JsonProperty override val companyId: UUID,
    @JsonProperty var verifyUsed: Int
) : ChartListItem

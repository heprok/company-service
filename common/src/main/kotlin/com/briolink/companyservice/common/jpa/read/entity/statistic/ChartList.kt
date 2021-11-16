package com.briolink.companyservice.common.jpa.read.entity.statistic

import com.briolink.companyservice.common.jpa.enumration.CompanyRoleTypeEnum
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

interface ChartListItem {
    val companyId: UUID
}

class ChartDataList<T>(
    @JsonProperty
    var name: String,
    @JsonProperty
    var items: MutableList<T>
)

@JsonIgnoreProperties(ignoreUnknown = true)
class ChartList<T> {
    @JsonProperty lateinit var data: MutableMap<String, ChartDataList<T>>
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartListItemWithRoles(
    @JsonProperty override val companyId: UUID,
    @JsonProperty val roles: MutableSet<String>
) : ChartListItem

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartListItemWithServicesCount(
    @JsonProperty override val companyId: UUID,
    @JsonProperty val companyRole: String,
    @JsonProperty val companyRoleType: CompanyRoleTypeEnum,
    @JsonProperty var servicesCount: Int
) : ChartListItem

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartListItemWithUsesCount(
    @JsonProperty override val companyId: UUID,
    @JsonProperty var usesCount: Int
) : ChartListItem

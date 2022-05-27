package com.briolink.companyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class CompanyStatistic(
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    @Deprecated("use totalProjects")
    val numberOfVerifications: Int,
    @JsonProperty
    val connectedPeoples: Int,
    @JsonProperty
    val connectedCompanies: Int,
    @JsonProperty
    val totalConnections: Int,
    @JsonProperty
    val servicesProvided: Int,
    @JsonProperty
    val totalProjects: Int,
    @JsonProperty
    val companyRoles: ArrayList<ProjectRoleData>
) : Domain

data class CompanyStatisticConnection(
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val connectedPeoples: Int,
    @JsonProperty
    val connectedCompanies: Int,
    @JsonProperty
    val totalConnections: Int,
) : Domain

data class ProjectRoleData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String,
    @JsonProperty
    val type: ProjectRoleType
)

enum class ProjectRoleType(val value: Int) {
    @JsonProperty("0")
    Buyer(0),

    @JsonProperty("1")
    Seller(1)
}

package com.briolink.companyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class CompanyStatistic(
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val numberOfVerifications: Int,
    @JsonProperty
    val companyRoles: ArrayList<ConnectionCompanyRoleData>
) : Domain

data class ConnectionCompanyRoleData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String,
    @JsonProperty
    val type: ConnectionCompanyRoleType
)

enum class ConnectionCompanyRoleType(val value: Int) {
    @JsonProperty("0")
    Buyer(0),

    @JsonProperty("1")
    Seller(1)
}

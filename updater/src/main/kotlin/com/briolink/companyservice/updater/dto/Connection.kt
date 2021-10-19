package com.briolink.companyservice.updater.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Year
import java.util.*

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class ConnectionStatus(val value: Int) {
    Draft(1),
    Pending(2),
    InProgress(3),
    Verified(4),
    Rejected(5)
}

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class RoleType {
    Buyer, Seller
}

data class ConnectionCompanyRole(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("type")
    val type: RoleType,
)

data class ConnectionService(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("serviceId")
    val serviceId: UUID,
    @JsonProperty("serviceName")
    val serviceName: String,
    @JsonProperty("startDate")
    val startDate: Year,
    @JsonProperty("endDate")
    val endDate: Year? = null,
)

data class ConnectionParticipant(
    @JsonProperty("userId")
    val userId: UUID,
    @JsonProperty("userJobPositionTitle")
    val userJobPositionTitle: String,
    @JsonProperty("companyId")
    val companyId: UUID,
    @JsonProperty("companyRole")
    val companyRole: ConnectionCompanyRole
)

data class Connection(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("participantFrom")
    var participantFrom: ConnectionParticipant,
    @JsonProperty("participantTo")
    var participantTo: ConnectionParticipant,
    @JsonProperty("services")
    val services: ArrayList<ConnectionService>,
    @JsonProperty("status")
    val status: ConnectionStatus,
)

package com.briolink.companyservice.updater.handler.connection

import com.briolink.event.Event
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.Year
import java.util.UUID

enum class ConnectionStatus(val value: Int) {
    @JsonProperty("1")
    Draft(1),

    @JsonProperty("2")
    Pending(2),

    @JsonProperty("3")
    InProgress(3),

    @JsonProperty("4")
    Verified(4),

    @JsonProperty("5")
    Rejected(5);
}

enum class ConnectionCompanyRoleType(val value: Int) {
    @JsonProperty("0")
    Buyer(0),

    @JsonProperty("1")
    Seller(1)
}

data class ConnectionCompanyRole(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String,
    @JsonProperty
    val type: ConnectionCompanyRoleType
)

data class ConnectionService(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val serviceId: UUID? = null,
    @JsonProperty
    val serviceName: String,
    @JsonProperty
    val startDate: Year,
    @JsonProperty
    val endDate: Year? = null,
)

data class ConnectionParticipant(
    @JsonProperty
    val userId: UUID,
    @JsonProperty
    val userJobPositionTitle: String,
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val companyRole: ConnectionCompanyRole,
)

data class Connection(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    var participantFrom: ConnectionParticipant,
    @JsonProperty
    var participantTo: ConnectionParticipant,
    @JsonProperty
    val services: ArrayList<ConnectionService> = arrayListOf(),
    @JsonProperty
    val status: ConnectionStatus,
    @JsonProperty
    var created: Instant,
)

data class ConnectionCreatedEvent(override val data: Connection) : Event<Connection>("1.0")
data class ConnectionUpdatedEvent(override val data: Connection) : Event<Connection>("1.0")

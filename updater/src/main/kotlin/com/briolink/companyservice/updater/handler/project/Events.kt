package com.briolink.companyservice.updater.handler.project

import com.briolink.lib.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.Year
import java.util.UUID

enum class ProjectStatus(val value: Int) {
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

enum class ProjectCompanyRoleType(val value: Int) {
    @JsonProperty("0")
    Buyer(0),

    @JsonProperty("1")
    Seller(1)
}

data class ProjectCompanyRole(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String,
    @JsonProperty
    val type: ProjectCompanyRoleType
)

data class ProjectService(
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

data class ProjectParticipant(
    @JsonProperty
    val userId: UUID,
    @JsonProperty
    val userJobPositionTitle: String,
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val companyRole: ProjectCompanyRole,
)

data class ProjectEventData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    var participantFrom: ProjectParticipant,
    @JsonProperty
    var participantTo: ProjectParticipant,
    @JsonProperty
    val services: ArrayList<ProjectService> = arrayListOf(),
    @JsonProperty
    val status: ProjectStatus,
    @JsonProperty
    var created: Instant,
)

data class ProjectCreatedEvent(override val data: ProjectEventData) : Event<ProjectEventData>("1.0")
data class ProjectUpdatedEvent(override val data: ProjectEventData) : Event<ProjectEventData>("1.0")
data class ProjectSyncEvent(override val data: SyncData<ProjectEventData>) : SyncEvent<ProjectEventData>("1.0")

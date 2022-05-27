package com.briolink.companyservice.updater.handler.project

import com.briolink.lib.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
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

enum class ProjectObjectType(val value: Int) {
    @JsonProperty("1")
    User(1),

    @JsonProperty("2")
    Company(2),

    @JsonProperty("3")
    CompanyService(3)
}

data class ProjectCompanyRoleData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String,
    @JsonProperty
    val type: ProjectCompanyRoleType
)

data class ProjectServiceData(
    @JsonProperty
    val serviceId: UUID? = null,
    @JsonProperty
    val serviceName: String,
    @JsonProperty
    val startDate: LocalDate? = null,
    @JsonProperty
    val endDate: LocalDate? = null,
)

data class ProjectParticipantData(
    @JsonProperty
    val userId: UUID? = null,
    @JsonProperty
    val companyId: UUID? = null,
    @JsonProperty
    val companyRole: ProjectCompanyRoleData? = null,
    @JsonProperty
    val companyAccepted: Boolean? = null,
    @JsonProperty
    val companyHidden: Boolean? = null,
    @JsonProperty
    val companyDeleted: Boolean? = null,
)

data class ProjectEventData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val participantFrom: ProjectParticipantData,
    @JsonProperty
    val participantTo: ProjectParticipantData,
    @JsonProperty
    val service: ProjectServiceData,
    @JsonProperty
    val status: ProjectStatus,
)

data class ProjectDeletedDataEventData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val objectType: ProjectObjectType,
    @JsonProperty
    val participantObjectId: UUID,
    @JsonProperty
    val anotherParticipantUserId: UUID,
    @JsonProperty
    val anotherParticipantCompanyId: UUID,
    @JsonProperty
    val serviceId: UUID?,
    @JsonProperty
    val completely: Boolean
)

data class ProjectVisibilityEventData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val serviceId: UUID?,
    @JsonProperty
    val objectType: ProjectObjectType,
    @JsonProperty
    val participantObjectId: UUID,
    @JsonProperty
    val anotherParticipantUserId: UUID,
    @JsonProperty
    val anotherParticipantCompanyId: UUID,
    @JsonProperty
    val hidden: Boolean
)

data class ProjectCreatedEvent(override val data: ProjectEventData) : Event<ProjectEventData>("1.0")
data class ProjectUpdatedEvent(override val data: ProjectEventData) : Event<ProjectEventData>("1.0")
data class ProjectSyncEvent(override val data: SyncData<ProjectEventData>) : SyncEvent<ProjectEventData>("1.0")

data class ProjectDeletedEvent(override val data: ProjectDeletedDataEventData) : Event<ProjectDeletedDataEventData>("1.0")
data class ProjectVisibilityUpdatedEvent(override val data: ProjectVisibilityEventData) : Event<ProjectVisibilityEventData>("1.0")

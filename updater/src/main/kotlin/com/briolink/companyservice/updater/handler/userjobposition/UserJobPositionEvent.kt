package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.event.Event
import com.briolink.lib.sync.ISyncData
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.UUID

data class UserJobPositionEventData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val title: String,
    @JsonProperty
    val startDate: LocalDate? = null,
    @JsonProperty
    val endDate: LocalDate? = null,
    @JsonProperty
    val isCurrent: Boolean = false,
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val userId: UUID,
)

data class UserJobPositionDeletedData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val userId: UUID,
    @JsonProperty
    val isCurrent: Boolean = false
)

data class UserJobPositionSyncData(
    @JsonProperty
    override val indexObjectSync: Long,
    @JsonProperty
    override val service: ServiceEnum,
    @JsonProperty
    override val syncId: Int,
    @JsonProperty
    override val totalObjectSync: Long,
    @JsonProperty
    override val objectSync: UserJobPositionEventData?,
) : ISyncData<UserJobPositionEventData>

data class UserJobPositionCreatedEvent(override val data: UserJobPositionEventData) :
    Event<UserJobPositionEventData>("1.0")

data class UserJobPositionUpdatedEvent(override val data: UserJobPositionEventData) :
    Event<UserJobPositionEventData>("1.0")

data class UserJobPositionDeletedEvent(override val data: UserJobPositionDeletedData) :
    Event<UserJobPositionDeletedData>("1.0")

data class UserJobPositionSyncEvent(override val data: UserJobPositionSyncData) : Event<UserJobPositionSyncData>("1.0")

package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.lib.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent
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

data class UserJobPositionCreatedEvent(override val data: UserJobPositionEventData) :
    Event<UserJobPositionEventData>("1.0")

data class UserJobPositionUpdatedEvent(override val data: UserJobPositionEventData) :
    Event<UserJobPositionEventData>("1.0")

data class UserJobPositionDeletedEvent(override val data: UserJobPositionDeletedData) :
    Event<UserJobPositionDeletedData>("1.0")

data class UserJobPositionSyncEvent(override val data: SyncData<UserJobPositionEventData>) : SyncEvent<UserJobPositionEventData>("1.0")

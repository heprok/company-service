package com.briolink.companyservice.updater.handler.expverification

import com.briolink.lib.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.util.UUID

data class ExpVerificationCreatedEvent(override val data: ExpVerificationEventData) : Event<ExpVerificationEventData>("1.0")
data class ExpVerificationUpdatedEvent(override val data: ExpVerificationEventData) : Event<ExpVerificationEventData>("1.0")

data class ExpVerificationSyncEvent(override val data: SyncData<ExpVerificationEventData>) : SyncEvent<ExpVerificationEventData>("1.0")

enum class ObjectConfirmType(@JsonValue val value: Int) {
    @JsonProperty("1")
    WorkExperience(1),

    @JsonProperty("2")
    Education(2);
}

enum class ExpVerificationStatus(@JsonValue val value: Int) {
    @JsonProperty("1")
    NotConfirmed(1),

    @JsonProperty("2")
    Pending(2),

    @JsonProperty("3")
    Confirmed(3),

    @JsonProperty("4")
    Rejected(4);
}

data class ExpVerificationEventData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val objectConfirmId: UUID,
    @JsonProperty
    val userToConfirmIds: ArrayList<UUID>,
    @JsonProperty
    val actionBy: UUID?,
    @JsonProperty
    val objectConfirmType: ObjectConfirmType,
    @JsonProperty
    val status: ExpVerificationStatus,
)

package com.briolink.companyservice.updater.handler.connection

import com.briolink.lib.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.util.UUID

enum class ConnectionObjectType(val value: Int) {
    @JsonProperty("1")
    User(1),

    @JsonProperty("2")
    Company(2);
}

enum class ConnectionStatus(@JsonValue val value: Int) {
    @JsonProperty("0")
    NotConnected(0),

    @JsonProperty("1")
    Pending(1),

    @JsonProperty("2")
    AwaitingResponse(2),

    @JsonProperty("3")
    Connected(3),

    @JsonProperty("4")
    Rejected(4);
}

data class ConnectionEventData(
    @JsonProperty
    val fromObjectId: UUID,
    @JsonProperty
    val fromObjectType: ConnectionObjectType,
    @JsonProperty
    val toObjectId: UUID?,
    @JsonProperty
    val toObjectType: ConnectionObjectType,
    @JsonProperty
    val status: ConnectionStatus,
    @JsonProperty
    val hidden: Boolean,
)

data class ConnectionCreatedEvent(override val data: ConnectionEventData) : Event<ConnectionEventData>("1.0")
data class ConnectionUpdatedEvent(override val data: ConnectionEventData) : Event<ConnectionEventData>("1.0")
data class ConnectionAcceptedEvent(override val data: ConnectionEventData) : Event<ConnectionEventData>("1.0")
data class ConnectionVisibilityUpdatedEvent(override val data: ConnectionEventData) : Event<ConnectionEventData>("1.0")
data class ConnectionDeletedEvent(override val data: ConnectionEventData) : Event<ConnectionEventData>("1.0")
data class ConnectionSyncEvent(override val data: SyncData<ConnectionEventData>) : SyncEvent<ConnectionEventData>("1.0")

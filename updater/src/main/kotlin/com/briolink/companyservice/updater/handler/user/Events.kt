package com.briolink.companyservice.updater.handler.user

import com.briolink.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.UUID

data class UserEventData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    var slug: String,
    @JsonProperty
    val firstName: String,
    @JsonProperty
    val lastName: String,
    @JsonProperty
    val description: String? = null,
    @JsonProperty
    val location: String? = null,
    @JsonProperty
    val image: URL? = null,
    @JsonProperty
    val twitter: String? = null,
    @JsonProperty
    val facebook: String? = null,
)

data class UserCreatedEvent(override val data: UserEventData) : Event<UserEventData>("1.0")
data class UserUpdatedEvent(override val data: UserEventData) : Event<UserEventData>("1.0")
data class UserSyncEvent(override val data: SyncData<UserEventData>) : SyncEvent<UserEventData>("1.0")

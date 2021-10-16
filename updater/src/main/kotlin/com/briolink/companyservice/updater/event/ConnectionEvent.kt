package com.briolink.companyservice.updater.event

import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.companyservice.updater.dto.Connection
import com.briolink.event.Event

data class ConnectionCreatedEvent(override val data: Connection) : Event<Connection>("1.0")
data class ConnectionUpdatedEvent(override val data: Connection) : Event<Connection>("1.0")

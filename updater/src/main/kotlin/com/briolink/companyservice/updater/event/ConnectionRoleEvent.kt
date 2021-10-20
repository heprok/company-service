package com.briolink.companyservice.updater.event

import com.briolink.companyservice.updater.dto.ConnectionRole
import com.briolink.event.Event

data class ConnectionRoleCreatedEvent(override val data: ConnectionRole) : Event<ConnectionRole>("1.0")

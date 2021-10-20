package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.ConnectionRoleReadRepository
import com.briolink.companyservice.updater.event.ConnectionRoleCreatedEvent

@EventHandler("ConnectionRoleCreatedEvent", "1.0")
class ConnectionRoleCreatedEventHandler(
    private val connectionRoleReadRepository: ConnectionRoleReadRepository
) : IEventHandler<ConnectionRoleCreatedEvent> {
    override fun handle(event: ConnectionRoleCreatedEvent) {
        val eventData = event.data
        val connectionRole = ConnectionRoleReadEntity(
                id = eventData.id,
                name = eventData.name,
                type = ConnectionRoleReadEntity.RoleType.valueOf(eventData.type.ordinal.toString()),
        )
        connectionRoleReadRepository.save(connectionRole)
    }
}

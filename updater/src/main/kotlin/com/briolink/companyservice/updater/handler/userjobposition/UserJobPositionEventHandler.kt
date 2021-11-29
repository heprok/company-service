package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler

@EventHandler("UserJobPositionCreatedEvent", "1.0")
class UserJobPositionCreatedEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
) : IEventHandler<UserJobPositionUpdatedEvent> {
    override fun handle(event: UserJobPositionUpdatedEvent) {
        userJobPositionHandlerService.create(event.data)
    }
}

@EventHandler("UserJobPositionUpdatedEvent", "1.0")
class UserJobPositionUpdatedEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
) : IEventHandler<UserJobPositionUpdatedEvent> {
    override fun handle(event: UserJobPositionUpdatedEvent) {
        userJobPositionHandlerService.update(event.data)
    }
}

@EventHandler("UserJobPositionDeletedEvent", "1.0")
class UserJobPositionDeletedEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
) : IEventHandler<UserJobPositionDeletedEvent> {
    override fun handle(event: UserJobPositionDeletedEvent) {
        userJobPositionHandlerService.delete(event.data.id)
    }
}

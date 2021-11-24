package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers

@EventHandlers(
    EventHandler("UserJobPositionCreatedEvent", "1.0"),
    EventHandler("UserJobPositionUpdatedEvent", "1.0"),
)
class UserJobPositionEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
) : IEventHandler<UserJobPositionUpdatedEvent> {
    override fun handle(event: UserJobPositionUpdatedEvent) {
        userJobPositionHandlerService.createOrUpdate(event.data)
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

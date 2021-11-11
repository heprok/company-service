package com.briolink.companyservice.updater.handler.user

import com.briolink.companyservice.updater.userjobposition.UserJobPositionHandlerService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers

@EventHandlers(
        EventHandler("UserCreatedEvent", "1.0"),
        EventHandler("UserUpdatedEvent", "1.0"),
)
class UserCreatedEventHandler(
    private val userHandlerService: UserHandlerService,
    private val userJobPositionHandlerService: UserJobPositionHandlerService
) : IEventHandler<UserCreatedEvent> {
    override fun handle(event: UserCreatedEvent) {
        userHandlerService.createOrUpdate(event.data).apply {
            if (event.name == "UserUpdatedEvent") {
                userJobPositionHandlerService.updateUser(this)
            }
        }
    }
}

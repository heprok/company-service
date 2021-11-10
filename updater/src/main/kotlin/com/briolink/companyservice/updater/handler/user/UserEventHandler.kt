package com.briolink.companyservice.updater.handler.user

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import org.springframework.transaction.annotation.Transactional

@EventHandlers(
        EventHandler("UserCreatedEvent", "1.0"),
        EventHandler("UserUpdatedEvent", "1.0")
)
class UserCreatedEventHandler(
    private val userHandlerService: UserHandlerService
) : IEventHandler<UserCreatedEvent> {
    override fun handle(event: UserCreatedEvent) {
        userHandlerService.createOrUpdate(event.data)
    }
}

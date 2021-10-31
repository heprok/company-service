package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.updater.event.UserCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository

@EventHandlers(
        EventHandler("UserCreatedEvent", "1.0"),
        EventHandler("UserUpdatedEvent", "1.0")
)

class UserCreatedEventHandler(
    private val userReadRepository: UserReadRepository
) : IEventHandler<UserCreatedEvent> {
    override fun handle(event: UserCreatedEvent) {
        val user = event.data
        userReadRepository.findById(user.id).orElse(
                UserReadEntity(
                        id = user.id,
                ),
        ).apply {
            data = UserReadEntity.Data(
                    lastName = user.lastName,
                    firstName = user.firstName,
                    image = user.image,
            ).apply {
                slug = user.slug
            }
        }
    }
}

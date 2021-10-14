package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.updater.event.UserCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository

@EventHandler("UserCreatedEvent", "1.0")
class UserCreatedEventHandler(
    private val userReadRepository: UserReadRepository
) : IEventHandler<UserCreatedEvent> {
    override fun handle(event: UserCreatedEvent) {
        val eventData = event.data
        val user = UserReadEntity(
                id = eventData.id,
                data = UserReadEntity.Data(
                        lastName = eventData.lastName,
                        firstName = eventData.firstName,
                        slug = eventData.slug,
                        image = eventData.image,
                ),
                )
        userReadRepository.save(user)
    }
}

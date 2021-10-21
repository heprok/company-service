package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.updater.event.UserCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.event.UserUpdatedEvent
import javax.persistence.EntityNotFoundException

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
                        image = eventData.image,
                ).apply {
                    slug = eventData.slug
                },
        )
        userReadRepository.save(user)
    }
}

@EventHandler("UserUpdatedEvent", "1.0")
class UserUpdatedEventHandler(
    private val userReadRepository: UserReadRepository
) : IEventHandler<UserUpdatedEvent> {
    override fun handle(event: UserUpdatedEvent) {
        val eventData = event.data
        val user = userReadRepository.findById(eventData.id)
                .orElseThrow { throw EntityNotFoundException(eventData.id.toString() + " user not found") }
        user.data = UserReadEntity.Data(
                lastName = eventData.lastName,
                firstName = eventData.firstName,
                image = eventData.image,
        ).apply {
            slug = eventData.slug
        }
        userReadRepository.save(user)
    }
}

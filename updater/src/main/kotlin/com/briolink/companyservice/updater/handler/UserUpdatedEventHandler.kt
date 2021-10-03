package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.event.v1_0.UserUpdatedEvent
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import javax.persistence.EntityNotFoundException

@EventHandler("UserUpdatedEvent", "1.0")
class UserUpdatedEventHandler(
    private val userReadRepository: UserReadRepository
) : IEventHandler<UserUpdatedEvent> {
    override fun handle(event: UserUpdatedEvent) {
        val eventData = event.data
        val user = userReadRepository.findById(eventData.id).orElseThrow{ throw EntityNotFoundException(eventData.id.toString() + " user not found") }
        user.data = UserReadEntity.Data(
                lastName = eventData.lastName,
                slug = eventData.slug,
                firstName = eventData.firstName,
                image = eventData.image,
        )
        userReadRepository.save(user)
    }
}

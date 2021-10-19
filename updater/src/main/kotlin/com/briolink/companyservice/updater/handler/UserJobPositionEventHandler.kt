package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.updater.event.UserJobPositionCreatedEvent
import com.briolink.companyservice.updater.event.UserJobPositionDeletedEvent
import com.briolink.companyservice.updater.event.UserJobPositionUpdatedEvent
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.dto.UserJobPosition
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import javax.persistence.EntityNotFoundException

@EventHandler("UserJobPositionCreatedEvent", "1.0")
class UserJobPositionCreatedEventHandler(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val userReadRepository: UserReadRepository
) : IEventHandler<UserJobPositionCreatedEvent> {
    override fun handle(event: UserJobPositionCreatedEvent) {
        val eventData = event.data
        if (eventData.endDate == null) {
            val user = userReadRepository.findById(eventData.userId)
                    .orElseThrow { throw EntityNotFoundException(eventData.userId.toString() + " user not found") }
            val userJobPosition = UserJobPositionReadEntity(
                    id = eventData.id,
                    userId = eventData.userId,
                    companyId = eventData.companyId,
                    data = UserJobPositionReadEntity.Data(
                            user = UserJobPositionReadEntity.User(
                                    lastName = user.data.lastName,
                                    firstName = user.data.firstName,
                                    slug = user.data.slug,
                                    image = user.data.image,
                            ),
                            title = eventData.title,
                    ),
            )
            userJobPositionReadRepository.save(userJobPosition)
        }
    }
}

@EventHandler("UserJobPositionUpdatedEvent", "1.0")
class UserJobPositionUpdatedEventHandler(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val userReadRepository: UserReadRepository
) : IEventHandler<UserJobPositionUpdatedEvent> {
    override fun handle(event: UserJobPositionUpdatedEvent) {
        val eventData = event.data
        if (eventData.endDate == null) {
            val jobPosition =
                    userJobPositionReadRepository.findById(eventData.id)
                            .orElseThrow { throw EntityNotFoundException(eventData.id.toString() + " job position not found") }
            jobPosition.companyId = eventData.companyId
            jobPosition.data.title = eventData.title
            userJobPositionReadRepository.save(jobPosition)
        } else {
            userJobPositionReadRepository.deleteById(eventData.id)
        }
    }
}

@EventHandler("UserJobPositionDeletedEvent", "1.0")
class UserJobPositionDeletedEventHandler(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
) : IEventHandler<UserJobPositionDeletedEvent> {
    override fun handle(event: UserJobPositionDeletedEvent) {
        val eventData = event.data
        userJobPositionReadRepository.deleteById(eventData.id)
    }
}

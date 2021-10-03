package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.event.v1_0.UserJobPositionCreatedEvent
import com.briolink.companyservice.common.event.v1_0.UserJobPositionDeletedEvent
import com.briolink.companyservice.common.event.v1_0.UserJobPositionUpdatedEvent
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import javax.persistence.EntityNotFoundException

@EventHandler("UserJobPositionCreatedEvent", "1.0")
class UserJobPositionCreatedEventHandler(
    private val userReadRepository: UserReadRepository
) : IEventHandler<UserJobPositionCreatedEvent> {
    override fun handle(event: UserJobPositionCreatedEvent) {
        val eventData = event.data
        if ( eventData.isCurrent ){
            val user = userReadRepository.findById(eventData.id).orElseThrow{ throw EntityNotFoundException(eventData.id.toString() + " user not found") }
            user.companyId = eventData.companyId
            user.data.jobPosition = eventData.title
            userReadRepository.save(user)
        }
    }
}

@EventHandler("UserJobPositionUpdatedEvent", "1.0")
class UserJobPositionUpdatedEventHandler(
    private val userReadRepository: UserReadRepository
) : IEventHandler<UserJobPositionUpdatedEvent> {
    override fun handle(event: UserJobPositionUpdatedEvent) {
        val eventData = event.data
        if ( eventData.isCurrent ){
            val user = userReadRepository.findById(eventData.id).orElseThrow{ throw EntityNotFoundException(eventData.id.toString() + " user not found") }
            user.companyId = eventData.companyId
            user.data.jobPosition = eventData.title
            userReadRepository.save(user)
        }
    }
}

@EventHandler("UserJobPositionDeletedEvent", "1.0")
class UserJobPositionDeletedEventHandler(
    private val userReadRepository: UserReadRepository
) : IEventHandler<UserJobPositionDeletedEvent> {
    override fun handle(event: UserJobPositionDeletedEvent) {
        val eventData = event.data
        if ( eventData.isCurrent ){
            val user = userReadRepository.findById(eventData.id).orElseThrow{ throw EntityNotFoundException(eventData.id.toString() + " user not found") }
            user.companyId = null
            user.data.jobPosition = null
            userReadRepository.save(user)
        }
    }
}

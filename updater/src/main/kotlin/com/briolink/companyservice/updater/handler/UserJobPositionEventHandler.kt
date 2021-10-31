package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.updater.event.UserJobPositionCreatedEvent
import com.briolink.companyservice.updater.event.UserJobPositionDeletedEvent
import com.briolink.companyservice.updater.event.UserJobPositionUpdatedEvent
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.handler.service.CompanyHandlerService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import javax.persistence.EntityNotFoundException

@EventHandlers(
        EventHandler("UserJobPositionCreatedEvent", "1.0"),
        EventHandler("UserJobPositionUpdatedEvent", "1.0")
)
class UserJobPositionEventHandler(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val userReadRepository: UserReadRepository,
    private val companyHandlerService: CompanyHandlerService
) : IEventHandler<UserJobPositionUpdatedEvent> {
    override fun handle(event: UserJobPositionUpdatedEvent) {
        val eventData = event.data
        if (eventData.endDate == null) {
            val jobPosition = userJobPositionReadRepository.findById(eventData.id).orElse(
                    UserJobPositionReadEntity(
                            id = eventData.id,
                            userId = eventData.userId,
                            companyId = eventData.companyId,
                    ).apply {
                        val user = userReadRepository.findById(eventData.userId)
                                .orElseThrow { throw EntityNotFoundException(eventData.userId.toString() + " user not found") }
                        data = UserJobPositionReadEntity.Data(
                                user = UserJobPositionReadEntity.User(
                                        firstName = user.data.firstName,
                                        lastName = user.data.lastName,
                                        slug = user.data.slug,
                                        image = user.data.image,
                                ),
                        )
                        companyHandlerService.setPermission(
                                companyId = eventData.companyId,
                                userId = eventData.userId,
                                roleType = UserPermissionRoleReadEntity.RoleType.Employee,
                        )
                    },
            )
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

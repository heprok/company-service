package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.updater.event.UserJobPositionCreatedEvent
import com.briolink.companyservice.updater.event.UserJobPositionDeletedEvent
import com.briolink.companyservice.updater.event.UserJobPositionUpdatedEvent
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.service.CompanyService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import javax.persistence.EntityNotFoundException

@EventHandler("UserJobPositionCreatedEvent", "1.0")
class UserJobPositionCreatedEventHandler(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val userReadRepository: UserReadRepository,
    private val companyService: CompanyService
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
            ).apply {
                data = UserJobPositionReadEntity.Data(
                        user = UserJobPositionReadEntity.User(
                                lastName = user.data.lastName,
                                firstName = user.data.firstName,
                                slug = user.data.slug,
                                image = user.data.image,
                        ),
                        title = eventData.title,
                )
            }
            companyService.setOwner(eventData.companyId, eventData.userId)
            userJobPositionReadRepository.save(userJobPosition)
        }
    }
}

@EventHandler("UserJobPositionUpdatedEvent", "1.0")
class UserJobPositionUpdatedEventHandler(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val userReadRepository: UserReadRepository,
    private val companyService: CompanyService
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
                    val user = userReadRepository.findById(eventData.userId).orElseThrow { throw EntityNotFoundException(eventData.userId.toString() + " user not found")}
                    data = UserJobPositionReadEntity.Data(
                            user = UserJobPositionReadEntity.User(
                                    firstName = user.data.firstName,
                                    lastName = user.data.lastName,
                                    slug = user.data.slug,
                                    image = user.data.image,
                            ),
                    )
                    companyService.setPermission(companyId = eventData.companyId, userId = eventData.userId, roleType = UserPermissionRoleReadEntity.RoleType.Employee)
                }
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

package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.companyservice.updater.handler.company.CompanyHandlerService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class UserJobPositionHandlerService(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val userReadRepository: UserReadRepository,
    private val companyHandlerService: CompanyHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun create(userJobPosition: UserJobPosition): UserJobPositionReadEntity {
        val userReadEntity = userReadRepository.findById(userJobPosition.userId)
            .orElseThrow { throw EntityNotFoundException(userJobPosition.userId.toString() + " user not found") }
        userJobPositionReadRepository.findByCompanyIdAndUserId(
            companyId = userJobPosition.companyId,
            userId = userJobPosition.userId
        ).let {
            if (it == null) {
                addEmployeeAndConnectionsHideOpen(
                    companyId = userJobPosition.companyId,
                    userId = userJobPosition.userId
                )
                UserJobPositionReadEntity(
                    id = userJobPosition.id,
                    companyId = userJobPosition.companyId,
                    userId = userJobPosition.userId,
                    endDate = userJobPosition.endDate,
                    data = UserJobPositionReadEntity.Data(
                        user = UserJobPositionReadEntity.User(
                            firstName = userReadEntity.data.firstName,
                            slug = userReadEntity.data.slug,
                            lastName = userReadEntity.data.lastName,
                            image = userReadEntity.data.image,
                        ),
                        status = UserJobPositionReadEntity.VerifyStatus.Verified,
                        verifiedBy = null,
                        isCurrent = userJobPosition.isCurrent,
                        startDate = userJobPosition.startDate,
                        endDate = userJobPosition.endDate,
                        title = userJobPosition.title
                    )
                )
            } else {
                if (userJobPosition.isCurrent) {
                    it.data.title = userJobPosition.title
                    it.data.startDate = userJobPosition.startDate
                    it.data.endDate = userJobPosition.endDate
                    it
                } else it
            }
        }.apply {
            return userJobPositionReadRepository.save(this)
        }
    }

    fun update(userJobPosition: UserJobPosition) {
        userJobPositionReadRepository.findById(userJobPosition.id).also {
            if (it.isEmpty) {
                create(userJobPosition)
            } else {
                it.get().apply {
                    if (this.companyId != userJobPosition.companyId || this.userId != userJobPosition.userId) {
                        userJobPositionReadRepository.findByCompanyIdAndUserId(
                            userId = userJobPosition.userId,
                            companyId = userJobPosition.companyId
                        ).apply {
                            if (this == null) create(userJobPosition)
                            else {
                                data.title = userJobPosition.title
                                data.startDate = userJobPosition.startDate
                                data.endDate = userJobPosition.endDate
                                data.isCurrent = userJobPosition.isCurrent
                                userJobPositionReadRepository.save(this)
                            }
                        }
                    } else {
                        data.title = userJobPosition.title
                        data.startDate = userJobPosition.startDate
                        data.endDate = userJobPosition.endDate
                        userJobPositionReadRepository.save(this)
                    }
                }
            }
        }
    }

    fun addEmployeeAndConnectionsHideOpen(userId: UUID, companyId: UUID) {
        companyHandlerService.addEmployee(
            companyId = companyId,
            userId = userId
        )
        connectionReadRepository.changeVisibilityByCompanyIdAndUserId(
            companyId = companyId,
            userId = userId, false,
        )
        applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(companyId))
    }

    fun updateUser(user: UserReadEntity) {
        userJobPositionReadRepository.updateUserByUserId(
            userId = user.id,
            slug = user.data.slug,
            firstName = user.data.firstName,
            lastName = user.data.lastName,
            image = user.data.image?.toString(),
        )
    }

    fun delete(userJobPositionId: UUID) {
        userJobPositionReadRepository.deleteById(userJobPositionId)
    }
}

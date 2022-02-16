package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.companyservice.common.jpa.read.entity.EmployeeReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.EmployeeReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.companyservice.updater.handler.company.CompanyHandlerService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
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
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val employeeReadRepository: EmployeeReadRepository,
) {
    fun createOrUpdate(userJobPositionEventData: UserJobPositionEventData) {
        val userReadEntity = userReadRepository.findById(userJobPositionEventData.userId)
            .orElseThrow { throw EntityNotFoundException(userJobPositionEventData.userId.toString() + " user not found") }
        var prevCompanyId: UUID? = null
        userJobPositionReadRepository.findById(userJobPositionEventData.id).also {
            if (it.isEmpty) {
                addEmployeeAndConnectionsHideOpen(
                    companyId = userJobPositionEventData.companyId,
                    userId = userJobPositionEventData.userId
                )
                userJobPositionReadRepository.save(
                    UserJobPositionReadEntity(
                        id = userJobPositionEventData.id,
                        companyId = userJobPositionEventData.companyId,
                        userId = userJobPositionEventData.userId,
                        endDate = userJobPositionEventData.endDate,
                        data = UserJobPositionReadEntity.Data(
                            user = UserJobPositionReadEntity.User(
                                firstName = userReadEntity.data.firstName,
                                slug = userReadEntity.data.slug,
                                lastName = userReadEntity.data.lastName,
                                image = userReadEntity.data.image,
                            ),
                            status = UserJobPositionReadEntity.VerifyStatus.Verified,
                            verifiedBy = null,
                            isCurrent = userJobPositionEventData.isCurrent,
                            startDate = userJobPositionEventData.startDate,
                            endDate = userJobPositionEventData.endDate,
                            title = userJobPositionEventData.title
                        )
                    )
                )
                refreshEmployeesByCompanyId(userJobPositionEventData.companyId)
            } else {
                it.get().apply {
                    if (companyId != userJobPositionEventData.companyId) {
                        prevCompanyId = companyId
                        companyId = userJobPositionEventData.companyId
                    }
                    endDate = userJobPositionEventData.endDate
                    data.isCurrent = userJobPositionEventData.isCurrent
                    data.startDate = userJobPositionEventData.startDate
                    data.endDate = userJobPositionEventData.endDate
                    data.title = userJobPositionEventData.title
                    userJobPositionReadRepository.save(this)
                }
                prevCompanyId?.let { refreshEmployeesByCompanyId(it) }
                refreshEmployeesByCompanyId(userJobPositionEventData.companyId)
            }
        }
    }

    fun refreshEmployeesByCompanyId(companyId: UUID) {
        employeeReadRepository.deleteAllByCompanyId(companyId)
        val userInCompany = mutableSetOf<UUID>()
        userJobPositionReadRepository.findByCompanyIdAndEndDateNull(companyId).sortedBy { it.data.isCurrent }.forEach {
            if (userInCompany.add(it.userId))
                employeeReadRepository.save(EmployeeReadEntity.fromUserJobPosition(it))
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
        ).also {
            if (it > 0) {
                println("PUBLISH EVENT TO RESFRESH STATS")
                applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(companyId, false))
            }
        }
    }

    fun updateUser(user: UserReadEntity) {
        employeeReadRepository.updateUser(
            userId = user.id,
            slug = user.data.slug,
            firstName = user.data.firstName,
            lastName = user.data.lastName,
            image = user.data.image?.toString(),
        )
        userJobPositionReadRepository.updateUserByUserId(
            userId = user.id,
            slug = user.data.slug,
            firstName = user.data.firstName,
            lastName = user.data.lastName,
            image = user.data.image?.toString(),
        )
    }

    fun delete(userJobPositionId: UUID) {
        userJobPositionReadRepository.findByIdOrNull(userJobPositionId)?.also {
            userJobPositionReadRepository.delete(it)
            refreshEmployeesByCompanyId(it.companyId)
        }
    }
}

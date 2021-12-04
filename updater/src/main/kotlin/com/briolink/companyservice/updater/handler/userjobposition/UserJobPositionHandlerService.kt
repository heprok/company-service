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
    fun createOrUpdate(userJobPosition: UserJobPosition) {
        val userReadEntity = userReadRepository.findById(userJobPosition.userId)
            .orElseThrow { throw EntityNotFoundException(userJobPosition.userId.toString() + " user not found") }
        var prevCompanyId: UUID? = null
        userJobPositionReadRepository.findById(userJobPosition.id).also {
            if (it.isEmpty) {
                addEmployeeAndConnectionsHideOpen(
                    companyId = userJobPosition.companyId,
                    userId = userJobPosition.userId
                )
                userJobPositionReadRepository.save(
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
                )
                refreshEmployeesByCompanyId(userJobPosition.companyId)
            } else {
                it.get().apply {
                    if (companyId != userJobPosition.companyId) {
                        prevCompanyId = companyId
                        companyId = userJobPosition.companyId
                    }
                    endDate = userJobPosition.endDate
                    data.isCurrent = userJobPosition.isCurrent
                    data.startDate = userJobPosition.startDate
                    data.endDate = userJobPosition.endDate
                    data.title = userJobPosition.title
                    userJobPositionReadRepository.save(this)
                }
                prevCompanyId?.let { refreshEmployeesByCompanyId(it) }
                refreshEmployeesByCompanyId(userJobPosition.id)
            }
        }
    }

//    fun create(userJobPosition: UserJobPosition): UserJobPositionReadEntity {
//        val userReadEntity = userReadRepository.findById(userJobPosition.userId)
//            .orElseThrow { throw EntityNotFoundException(userJobPosition.userId.toString() + " user not found") }
//
//        addEmployeeAndConnectionsHideOpen(
//            companyId = userJobPosition.companyId,
//            userId = userJobPosition.userId
//        )
//
//        return userJobPositionReadRepository.save(
//            UserJobPositionReadEntity(
//                id = userJobPosition.id,
//                companyId = userJobPosition.companyId,
//                userId = userJobPosition.userId,
//                endDate = userJobPosition.endDate,
//                data = UserJobPositionReadEntity.Data(
//                    user = UserJobPositionReadEntity.User(
//                        firstName = userReadEntity.data.firstName,
//                        slug = userReadEntity.data.slug,
//                        lastName = userReadEntity.data.lastName,
//                        image = userReadEntity.data.image,
//                    ),
//                    status = UserJobPositionReadEntity.VerifyStatus.Verified,
//                    verifiedBy = null,
//                    isCurrent = userJobPosition.isCurrent,
//                    startDate = userJobPosition.startDate,
//                    endDate = userJobPosition.endDate,
//                    title = userJobPosition.title
//                )
//            )
//        ).also { userJobPositionReadEntity ->
//            if (userJobPosition.isCurrent || userJobPosition.endDate == null) {
//                val employeePK = EmployeePK(userJobPosition.companyId, userJobPosition.userId)
//                val employeeReadEntity = employeeReadRepository.findById(employeePK)
//                if (employeeReadEntity.isEmpty) {
//                    EmployeeReadEntity.fromUserJobPosition(userJobPositionReadEntity)
//                        .also { employeeReadRepository.save(it) }
//                } else {
//                    employeeReadEntity.get().apply {
//                        if (userJobPositionId != userJobPosition.id) {
//                            if (userJobPosition.isCurrent) {
//                                userJobPositionId = userJobPosition.id
//                                data.jobPosition = userJobPosition.title
//                                employeeReadRepository.save(this)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    fun update(userJobPosition: UserJobPosition) {
//        userJobPositionReadRepository.findById(userJobPosition.id).also {
//            if (it.isEmpty) create(userJobPosition)
//            else {
//                val prevCompanyId: UUID
//                val userJobPositionReadEntity = it.get().apply {
//                    prevCompanyId = companyId
//                    companyId = userJobPosition.companyId
//                    data.title = userJobPosition.title
//                    data.startDate = userJobPosition.startDate
//                    data.endDate = userJobPosition.endDate
//                    data.isCurrent = userJobPosition.isCurrent
//                    userJobPositionReadRepository.save(this)
//                }
//                if (userJobPosition.isCurrent || userJobPosition.endDate == null) {
//                    val employeePK = EmployeePK(userJobPosition.companyId, userJobPosition.userId)
//                    val employeeReadEntity = employeeReadRepository.findById(employeePK)
//                    if (userJobPosition.endDate == null && employeeReadEntity.isEmpty) {
//                        employeeReadRepository.save(EmployeeReadEntity.fromUserJobPosition(userJobPositionReadEntity))
//                        return
//                    }
//                    employeeReadEntity.get().apply {
//                        if (userJobPositionId != userJobPosition.id && userJobPosition.isCurrent) {
//                            userJobPositionId = userJobPosition.id
//                            data.jobPosition = userJobPosition.title
//                            employeeReadRepository.save(this)
//                        }
//                    }
//                }
//            }
//        }
//    }

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
        )
        applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(companyId))
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
        userJobPositionReadRepository.deleteById(userJobPositionId)
    }
}

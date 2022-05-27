package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.companyservice.common.jpa.enumeration.ExpVerificationStatusEnum
import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.EmployeeReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRoleEnum
import com.briolink.lib.permission.exception.exist.PermissionRoleExistException
import com.briolink.lib.permission.exception.notfound.UserPermissionRoleNotFoundException
import com.briolink.lib.permission.service.PermissionService
import com.vladmihalcea.hibernate.type.range.Range
import com.vladmihalcea.hibernate.type.util.ObjectMapperWrapper
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
    private val permissionService: PermissionService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val employeeReadRepository: EmployeeReadRepository,
    private val companyReadRepository: CompanyReadRepository,
) {
    fun createOrUpdate(userJobPosition: UserJobPositionEventData) {
        val userReadEntity = userReadRepository.findById(userJobPosition.userId)
            .orElseThrow { throw EntityNotFoundException(userJobPosition.userId.toString() + " user not found") }
        var prevCompanyId: UUID? = null

        userJobPositionReadRepository.findById(userJobPosition.id).also { userJobPositionReadEntityOptional ->
            if (userJobPositionReadEntityOptional.isEmpty) {
                userJobPositionReadRepository.save(
                    UserJobPositionReadEntity(
                        id = userJobPosition.id,
                        companyId = userJobPosition.companyId,
                        userId = userJobPosition.userId,
                        _status = ExpVerificationStatusEnum.NotConfirmed.value,
                    ).apply {
                        jobTitle = userJobPosition.title
                        jobTitleTsv = jobTitle

                        userFullName = userReadEntity.fullName
                        userFullNameTsv = userFullName

                        dates = if (userJobPosition.endDate == null) Range.closedInfinite(userJobPosition.startDate)
                        else Range.open(userJobPosition.startDate, userJobPosition.endDate)
                        if (userJobPosition.isCurrent)
                            userJobPositionReadRepository.removeCurrent(userJobPosition.userId)
                        isCurrent = userJobPosition.isCurrent

                        data = UserJobPositionReadEntity.Data(
                            user = UserJobPositionReadEntity.User(
                                firstName = userReadEntity.data.firstName,
                                slug = userReadEntity.data.slug,
                                lastName = userReadEntity.data.lastName,
                                image = userReadEntity.data.image,
                            ),
                            verifiedBy = null,
                            jobPosition = UserJobPositionReadEntity.UserJobPosition(
                                id = id,
                                title = jobTitle,
                                startDate = dates.lower(),
                                endDate = if (dates.hasUpperBound()) dates.upper() else null,
                            ),
                        )
                    },
                )

                // if (userJobPosition.endDate != null &&  addEmployee(userJobPosition.userId, userJobPosition.companyId))
                //     hideConnection(userJobPosition.userId, userJobPosition.companyId, false)
                //
                // refreshEmployeesByCompanyId(userJobPosition.companyId)
            } else {
                userJobPositionReadEntityOptional.get().apply {
                    if (companyId != userJobPosition.companyId) {
                        prevCompanyId = companyId
                        companyId = userJobPosition.companyId
                    }
                    dates = if (userJobPosition.endDate == null) Range.closedInfinite(userJobPosition.startDate)
                    else Range.open(userJobPosition.startDate, userJobPosition.endDate)
                    isCurrent = userJobPosition.isCurrent
                    userFullNameTsv = userFullName
                    jobTitle = userJobPosition.title
                    jobTitleTsv = jobTitle
                    userJobPositionReadRepository.save(this)
                }

                prevCompanyId?.let {
                    // if (addEmployee(userJobPosition.userId, userJobPosition.companyId))
                    //     hideConnection(userJobPosition.userId, userJobPosition.companyId, false)
                    deleteUserPermission(userJobPosition.userId, it)
                }
                if (userJobPosition.endDate != null) {
                    deleteUserPermission(userJobPosition.userId, userJobPosition.companyId)
                }
                // refreshEmployeesByCompanyId(userJobPosition.companyId)
            }
        }
        updateUserPermission(userJobPosition.userId, userJobPosition.companyId)
    }

    fun refreshEmployeesByCompanyId(companyId: UUID) {
        employeeReadRepository.deleteAllByCompanyId(companyId)
        employeeReadRepository.refreshEmployeesByCompanyId(companyId)
    }

    private fun addEmployee(userId: UUID, companyId: UUID): Boolean {
        return try {
            permissionService.createPermissionRole(
                userId = userId,
                accessObjectType = AccessObjectTypeEnum.Company,
                accessObjectId = companyId,
                permissionRole = PermissionRoleEnum.Employee,
            ).also {
                updateUserPermission(userId, companyId)
            } != null
        } catch (role: PermissionRoleExistException) {
            false
        }
    }

    fun hideConnection(userId: UUID, companyId: UUID, hidden: Boolean = false) {
        connectionReadRepository.changeVisibilityByCompanyIdAndUserId(
            companyId = companyId, userId = userId, hidden = hidden,
        ).also {
            if (it > 0) {
                applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(companyId, false))
            }
        }
    }

    fun updateStatus(id: UUID, status: ExpVerificationStatusEnum, verifiedBy: UUID? = null) {
        userJobPositionReadRepository.findById(id).ifPresent { entity ->
            entity.status = status
            entity.data.verifiedBy = if (status == ExpVerificationStatusEnum.Confirmed)
                verifiedBy else null

            userJobPositionReadRepository.save(entity).also {
                if (status != ExpVerificationStatusEnum.Confirmed) {
                    deleteUserPermission(entity.userId, entity.companyId)
                } else {
                    if (addEmployee(entity.userId, entity.companyId))
                        hideConnection(entity.userId, entity.companyId, false)
                    updateUserPermission(entity.userId, entity.companyId)
                }
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

    fun updateVerification(id: UUID, verificationId: UUID?, verifyByCompany: Boolean) =
        userJobPositionReadRepository.updateVerification(id, verificationId, verifyByCompany)

    fun deleteUserPermission(userId: UUID, companyId: UUID) {
        if (companyReadRepository.existsByIdAndCreatedBy(companyId, userId)) return
        if (!userJobPositionReadRepository.existsByCompanyIdAndUserIdAndStatusAndEndDateIsNull(
                companyId = companyId,
                userId = userId,
                status = ExpVerificationStatusEnum.Confirmed.value,
            )
        ) {
            try {
                permissionService.deletePermissionRole(
                    userId = userId,
                    accessObjectType = AccessObjectTypeEnum.Company,
                    accessObjectId = companyId,
                )
                if (userJobPositionReadRepository.deleteUserPermission(userId, companyId) > 0) refreshEmployeesByCompanyId(companyId)
            } catch (_: UserPermissionRoleNotFoundException) {
            }
        }
    }

    fun delete(userJobPositionId: UUID) {
        userJobPositionReadRepository.findByIdOrNull(userJobPositionId)?.also { readEntity ->
            userJobPositionReadRepository.delete(readEntity).also {
                deleteUserPermission(readEntity.userId, readEntity.companyId)
                refreshEmployeesByCompanyId(readEntity.companyId)
            }
        }
    }

    fun updateUserPermission(userId: UUID, companyId: UUID) {
        permissionService.getUserPermissionRights(userId, companyId, AccessObjectTypeEnum.Company).also { userPermissionRights ->
            if (userPermissionRights == null) {
                if (userJobPositionReadRepository.deleteUserPermission(userId, companyId) > 0) refreshEmployeesByCompanyId(companyId)
                return
            }

            if (userJobPositionReadRepository.updateUserPermission(
                    userId = userId,
                    companyId = companyId,
                    level = userPermissionRights.permissionRole.level,
                    permissionRoleId = userPermissionRights.permissionRole.id,
                    enabledPermissionRightsJson =
                    ObjectMapperWrapper.INSTANCE.objectMapper.writeValueAsString(userPermissionRights.permissionRights),
                    rights = userPermissionRights.permissionRights.map { it.action }.toTypedArray(),
                ) > 0
            )
                employeeReadRepository.updateUserPermission(
                    userId = userId,
                    companyId = companyId,
                    permissionRoleId = userPermissionRights.permissionRole.id,
                    enabledPermissionRightsJson =
                    ObjectMapperWrapper.INSTANCE.objectMapper.writeValueAsString(userPermissionRights.permissionRights),
                )
        }
    }
}

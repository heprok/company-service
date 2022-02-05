package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.companyservice.common.jpa.enumeration.UserJobPositionVerifyStatusEnum
import com.briolink.companyservice.common.jpa.read.entity.EmployeeReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.EmployeeReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRoleEnum
import com.briolink.lib.permission.exception.exist.PermissionRoleExistException
import com.briolink.lib.permission.service.PermissionService
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
) {
    fun createOrUpdate(userJobPosition: UserJobPosition) {
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
                        endDate = userJobPosition.endDate,
                        // TODO replace verify status
                        _status = UserJobPositionVerifyStatusEnum.Verified.value,
                        data = UserJobPositionReadEntity.Data(
                            user = UserJobPositionReadEntity.User(
                                firstName = userReadEntity.data.firstName,
                                slug = userReadEntity.data.slug,
                                lastName = userReadEntity.data.lastName,
                                image = userReadEntity.data.image,
                            ),
                            verifiedBy = null,
                            isCurrent = userJobPosition.isCurrent,
                            startDate = userJobPosition.startDate,
                            endDate = userJobPosition.endDate,
                            title = userJobPosition.title
                        )
                    )
                )

                if (addEmployee(userJobPosition.companyId, userJobPosition.userId))
                    hideConnection(userJobPosition.companyId, userJobPosition.userId, false)
                refreshEmployeesByCompanyId(userJobPosition.companyId)
            } else {
                userJobPositionReadEntityOptional.get().apply {
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
                refreshEmployeesByCompanyId(userJobPosition.companyId)
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

    private fun addEmployee(userId: UUID, companyId: UUID): Boolean {
        return try {
            permissionService.createPermissionRole(
                userId = userId,
                accessObjectType = AccessObjectTypeEnum.Company,
                accessObjectId = companyId,
                permissionRole = PermissionRoleEnum.Employee
            ) != null
        } catch (_: PermissionRoleExistException) {
            false
        }
    }

    fun hideConnection(userId: UUID, companyId: UUID, hidden: Boolean = false) {
        connectionReadRepository.changeVisibilityByCompanyIdAndUserId(
            companyId = companyId, userId = userId, hidden = hidden,
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

    fun deleteUserPermission(userId: UUID, companyId: UUID) {
        userJobPositionReadRepository.deleteUserPermission(userId, companyId).also {
            if (it > 0)
                refreshEmployeesByCompanyId(companyId)
        }
    }

    fun addUserPermission(userPermission: UserPermissionRoleReadEntity) {
        userJobPositionReadRepository.updateUserPermission(
            userId = userPermission.userId,
            companyId = userPermission.accessObjectUuid,
            level = userPermission.data.level,
            permissionRoleId = userPermission.role.id,
            enabledPermissionRightsJson = ObjectMapperWrapper.INSTANCE.objectMapper.writeValueAsString(userPermission.data.enabledPermissionRights) // ktlint-disable max-line-length
        ).also {
            if (it > 0)
                refreshEmployeesByCompanyId(userPermission.accessObjectUuid)
        }
//        userJobPositionReadRepository.findByCompanyIdAndUserId(
//            userId = userPermission.userId,
//            companyId = userPermission.accessObjectUuid
//        ).forEach {
//            it.data.userPermission = UserJobPositionReadEntity.UserPermission(
//                role = userPermission.role,
//                level = userPermission.data.level,
//                rights = userPermission.data.enabledPermissionRights
//            )
//            userJobPositionReadRepository.save(it)
//        }
    }
}

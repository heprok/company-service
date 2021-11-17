package com.briolink.companyservice.api.service

import com.briolink.companyservice.api.graphql.SecurityUtil
import com.briolink.companyservice.common.jpa.enumration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumration.PermissionRightEnum
import com.briolink.companyservice.common.jpa.enumration.UserPermissionRoleTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.event.publisher.EventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class PermissionService(
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val eventPublisher: EventPublisher,
) {
    fun create(
        accessObjectType: AccessObjectTypeEnum,
        accessObjectUuid: UUID,
        userId: UUID = SecurityUtil.currentUserAccountId,
        roleType: UserPermissionRoleTypeEnum
    ): UserPermissionRoleReadEntity {
        return userPermissionRoleReadRepository.getUserPermissionRoleByRole(
                accessObjectUuid = accessObjectUuid,
                accessObjectType = accessObjectType.value,
                userId = userId,
                userPermissionRoleType = roleType.value,
        ) ?: UserPermissionRoleReadEntity(
                accessObjectUuid = accessObjectUuid,
                userId = userId,
                _accessObjectType = accessObjectType.value,
                _role = roleType.value,
        ).let {
            userPermissionRoleReadRepository.save(it)
        }

    }

    fun update(
        id: UUID,
        accessObjectType: AccessObjectTypeEnum,
        accessObjectUuid: UUID,
        userId: UUID = SecurityUtil.currentUserAccountId,
        roleType: UserPermissionRoleTypeEnum
    ): UserPermissionRoleReadEntity =
            userPermissionRoleReadRepository.findById(id).orElseThrow {
                throw EntityNotFoundException("Not found permission with id: $id")
            }.apply {
                this.accessObjectUuid = accessObjectUuid
                this.userId = userId
                this.accessObjectType = accessObjectType
                this.role = roleType
                userPermissionRoleReadRepository.save(this)
            }

    fun isHavePermission(
        userId: UUID = SecurityUtil.currentUserAccountId,
        companyId: UUID,
        accessObjectType: AccessObjectTypeEnum,
        permissionRight: PermissionRightEnum
    ): Boolean {
        userPermissionRoleReadRepository.findByAccessObjectUuidAndUserId(userId = userId, accessObjectUuid = companyId)
                .let { listPermission ->
                    listPermission.forEach {
                        if (it.role == UserPermissionRoleTypeEnum.Owner && it.accessObjectType == accessObjectType) return true
                        if (it.accessObjectType == AccessObjectTypeEnum.Company && it.role == UserPermissionRoleTypeEnum.Owner) return true
                        if (it.role == UserPermissionRoleTypeEnum.Employee && permissionRight == PermissionRightEnum.VerifyCollegue) return true
                    }
                }
        return false
    }

    fun addAllPermissionByUserId(userId: UUID) {
        companyReadRepository.findAll().forEach {
            create(
                    AccessObjectTypeEnum.Company, it.id, userId = userId, UserPermissionRoleTypeEnum.Owner
            )
        }
    }
}

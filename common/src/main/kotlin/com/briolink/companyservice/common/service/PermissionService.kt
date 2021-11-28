package com.briolink.companyservice.common.service

import com.briolink.companyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumeration.PermissionRightEnum
import com.briolink.companyservice.common.jpa.enumeration.UserPermissionRoleTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class PermissionService(
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    private val companyReadRepository: CompanyReadRepository,
) {
    fun createPermission(
        accessObjectType: AccessObjectTypeEnum,
        accessObjectUuid: UUID,
        userId: UUID,
        roleType: UserPermissionRoleTypeEnum
    ): UserPermissionRoleReadEntity {
        return userPermissionRoleReadRepository.getUserPermissionRole(
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

//    fun editRole(accessObjectUuid: UUID, userId: UUID, roleType: UserPermissionRoleTypeEnum): UserPermissionRoleReadEntity {
//        return userPermissionRoleReadRepository.findByAccessObjectUuidAndUserId(accessObjectUuid = accessObjectUuid, userId = userId)
//    }

    fun updatePermission(
        id: UUID,
        accessObjectType: AccessObjectTypeEnum,
        accessObjectUuid: UUID,
        userId: UUID,
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
        userId: UUID,
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

    // TODO удалить
    fun addAllPermissionByUserId(userId: UUID) {
        companyReadRepository.getAllCompanyUUID().forEach {
            createPermission(AccessObjectTypeEnum.Company, it, userId = userId, UserPermissionRoleTypeEnum.Owner)
        }
    }
}

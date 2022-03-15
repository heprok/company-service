package com.briolink.companyservice.updater.handler.userpermission

import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class UserPermissionHandlerService(
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
) {
    fun createOrUpdateCompanyPermission(eventData: UserPermissionEventData): UserPermissionRoleReadEntity =
        userPermissionRoleReadRepository.findById(eventData.id).orElse(
            UserPermissionRoleReadEntity(
                eventData.id,
                accessObjectUuid = eventData.accessObjectId,
                userId = eventData.userId,
                _accessObjectType = eventData.accessObjectType.id,
                _role = eventData.permissionRole.role.id,
                data = UserPermissionRoleReadEntity.Data(
                    level = eventData.permissionRole.level,
                    enabledPermissionRights = eventData.enablePermissionRights
                ),
            )
        ).apply {
            accessObjectUuid = eventData.accessObjectId
            userId = eventData.userId
            accessObjectType = eventData.accessObjectType
            role = eventData.permissionRole.role
            data.level = eventData.permissionRole.level
            data.enabledPermissionRights = eventData.enablePermissionRights
            userPermissionRoleReadRepository.save(this)
        }

    fun deletePermission(id: UUID) {
        userPermissionRoleReadRepository.deleteById(id)
    }
}

package com.briolink.companyservice.api.util

import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.briolink.lib.permission.service.PermissionService
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PermissionUtil(
    private val permissionService: PermissionService
) {
    fun check(companyId: UUID, right: PermissionRightEnum): Boolean =
        !permissionService.isHavePermission(
            userId = SecurityUtil.currentUserAccountId,
            accessObjectType = AccessObjectTypeEnum.Company,
            accessObjectId = UUID.randomUUID(),
            permissionRight = right
        )
}

package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserPermissionRoleReadRepository : JpaRepository<UserPermissionRoleReadEntity, UUID> {


    fun findByAccessObjectUuidAndAccessObjectTypeAndUserId(
        accessObjectUuid: UUID,
        accessObjectType: Int = 1,
        userId: UUID
    ): UserPermissionRoleReadEntity?

}

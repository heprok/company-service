package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.enumration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumration.UserPermissionRoleTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface UserPermissionRoleReadRepository : JpaRepository<UserPermissionRoleReadEntity, UUID> {

    @Query(
            """
        SELECT c 
        FROM UserPermissionRoleReadEntity c
        WHERE 
            c.accessObjectUuid = :accessObjectUuid AND
            c.userId = :userId AND
            c._accessObjectType = :accessObjectType
    """,
    )
    fun getUserPermissionRole(
        @Param("accessObjectUuid") accessObjectUuid: UUID,
        @Param("accessObjectType") accessObjectType: Int = AccessObjectTypeEnum.Company.value,
        @Param("userId") userId: UUID
    ): UserPermissionRoleReadEntity?



//    fun existsByCompanyId(
//        accessObjectUuid: UUID,
//        _accessObjectType: Int = AccessObjectTypeEnum.Company.value,
//        _role: Int = UserPermissionRoleTypeEnum.Owner.value
//    ): Boolean

}

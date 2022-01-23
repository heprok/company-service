package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

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
        @Param("accessObjectType") accessObjectType: Int,
        @Param("userId") userId: UUID
    ): UserPermissionRoleReadEntity?

    @Query(
        """
        SELECT c 
        FROM UserPermissionRoleReadEntity c
        WHERE 
            c.accessObjectUuid = :accessObjectUuid AND
            c.userId = :userId AND
            c._accessObjectType = :accessObjectType AND
            c._role = :userPermissionRoleType

    """,
    )
    fun getUserPermissionRole(
        @Param("accessObjectUuid") accessObjectUuid: UUID,
        @Param("accessObjectType") accessObjectType: Int,
        @Param("userId") userId: UUID,
        @Param("userPermissionRoleType") userPermissionRoleType: Int
    ): UserPermissionRoleReadEntity?

    @Query(
        """SELECT count(c.id) > 0
                FROM UserPermissionRoleReadEntity c
                WHERE 
                    c.accessObjectUuid = :accessObjectUuid AND
                    c.userId = :userId AND
                    c._accessObjectType = :accessObjectType AND 
                    c._role = :userPermissionRoleType
            """,
    )
    fun existsRole(
        @Param("accessObjectUuid") accessObjectUuid: UUID,
        @Param("accessObjectType") accessObjectType: Int,
        @Param("userId") userId: UUID,
        @Param("userPermissionRoleType") userPermissionRoleType: Int
    ): Boolean

//    fun existsByCompanyId(
//        accessObjectUuid: UUID,
//        _accessObjectType: Int = AccessObjectTypeEnum.Company.value,
//        _role: Int = UserPermissionRoleTypeEnum.Owner.value
//    ): Boolean

    fun findByAccessObjectUuidAndUserId(accessObjectUuid: UUID, userId: UUID): List<UserPermissionRoleReadEntity>

    @Modifying
    @Query("DELETE UserPermissionRoleReadEntity c WHERE c.id = ?1")
    override fun deleteById(id: UUID)

    fun existsByAccessObjectUuidAndUserId(accessObjectUuid: UUID, userId: UUID): Boolean
}

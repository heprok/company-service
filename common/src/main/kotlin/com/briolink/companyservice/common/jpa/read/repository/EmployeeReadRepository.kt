package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.EmployeePK
import com.briolink.companyservice.common.jpa.read.entity.EmployeeReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface EmployeeReadRepository : JpaRepository<EmployeeReadEntity, EmployeePK> {
    @Modifying
    @Query(
        """update EmployeeReadEntity u
           set u.data = 
                   function('jsonb_sets', u.data,
                           '{user,slug}', :slug, text,
                           '{user,image}', :image, text,
                           '{user,firstName}', :firstName, text,
                           '{user,lastName}', :lastName, text
                   )
           where u.userId = :userId""",
    )
    fun updateUser(
        @Param("userId") userId: UUID,
        @Param("slug") slug: String,
        @Param("firstName") firstName: String,
        @Param("lastName") lastName: String,
        @Param("image") image: String? = null,
    )

    @Modifying
    @Query(
        """UPDATE EmployeeReadEntity u
           SET u.data = function('jsonb_sets', u.data,
                '{userPermission,permissionRole}', :permissionRoleId, int,
                '{userPermission,permissionRights}', :enabledPermissionRightsJson, jsonb
            )
            WHERE u.userId = :userId AND u.companyId = :companyId
        """,
    )
    fun updateUserPermission(
        @Param("userId") userId: UUID,
        @Param("companyId") companyId: UUID,
        @Param("permissionRoleId") permissionRoleId: Int,
        @Param("enabledPermissionRightsJson") enabledPermissionRightsJson: String,
    ): Int

    @Modifying
    @Query(
        """DELETE FROM EmployeeReadEntity e WHERE e.companyId = ?1""",
    )
    fun deleteAllByCompanyId(companyId: UUID)

    fun findByCompanyId(companyId: UUID, pageable: Pageable): Page<EmployeeReadEntity>

    @Modifying
    @Query(
        """
                INSERT INTO read.employee(user_id, company_id, user_job_position_id, data)
                SELECT
                    distinct on (user_id) user_id,  company_id, id, data
                FROM
                    read.user_job_position
                WHERE
                    (is_current = true OR upper(dates) is null) AND company_id = ?1
                ORDER BY user_id, is_current DESC, upper(dates) DESC
            """,
        nativeQuery = true,
    )
    fun refreshEmployeesByCompanyId(companyId: UUID)
}

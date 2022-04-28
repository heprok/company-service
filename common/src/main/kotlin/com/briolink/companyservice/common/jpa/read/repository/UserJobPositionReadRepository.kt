package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserJobPositionReadRepository : JpaRepository<UserJobPositionReadEntity, UUID> {
    fun findByCompanyIdAndUserId(companyId: UUID, userId: UUID): List<UserJobPositionReadEntity>

    @Modifying
    @Query(
        """UPDATE UserJobPositionReadEntity u
           SET u.isCurrent = false
           WHERE u.userId = ?1
        """,
    )
    fun removeCurrent(userId: UUID): Int

    @Modifying
    @Query(
        """UPDATE UserJobPositionReadEntity u

           SET u.data = function('jsonb_sets', u.data,
                '{user,slug}', :slug, text,
                '{user,firstName}', :firstName, text,
                '{user,lastName}', :lastName, text,
                '{user,image}', :image, text
           ),
                u.userFullName = CONCAT(:firstName, ' ', :lastName),
                u.userFullNameTsv = to_tsvector('simple', CONCAT(:firstName, ' ', :lastName))
            where u.userId = :userId""",
    )
    fun updateUserByUserId(
        @Param("userId") userId: UUID,
        @Param("slug") slug: String?,
        @Param("firstName") firstName: String?,
        @Param("lastName") lastName: String?,
        @Param("image") image: String?,
    )

    @Modifying
    @Query(
        """UPDATE UserJobPositionReadEntity u
           SET u.data = function('jsonb_sets', u.data,
                '{userPermission}', null, text
            ),
            u.permissionLevel = null,
            u.rights = null
            WHERE u.userId = ?1 AND u.companyId = ?2
        """,
    )
    fun deleteUserPermission(
        userId: UUID,
        companyId: UUID
    ): Int

    @Modifying
    @Query(
        """UPDATE UserJobPositionReadEntity u
           SET u.data = function('jsonb_sets', u.data,
                '{userPermission}', '{}', jsonb,
                '{userPermission,permissionRole}', :permissionRoleId, int,
                '{userPermission,permissionRights}', :enabledPermissionRightsJson, jsonb
            ),
                u.permissionLevel = :level,
                u.rights = :rights
            WHERE u.userId = :userId AND u.companyId = :companyId
        """,
    )
    fun updateUserPermission(
        @Param("userId") userId: UUID,
        @Param("companyId") companyId: UUID,
        @Param("level") level: Int,
        @Param("permissionRoleId") permissionRoleId: Int,
        @Param("enabledPermissionRightsJson") enabledPermissionRightsJson: String,
        @Param("rights") rights: Array<String>
    ): Int

    @Query(
        """SELECT u
           FROM UserJobPositionReadEntity as u
           WHERE upper(u.dates) is null AND companyId = ?1 AND u.dates != 'empty'
        """,
    )
    fun findByCompanyIdAndEndDateNull(companyId: UUID): List<UserJobPositionReadEntity>

    @Modifying
    @Query("""DELETE FROM UserJobPositionReadEntity u WHERE u.userId = ?2 AND u.companyId = ?1""")
    fun deleteByCompanyIdAndUserId(companyId: UUID, userId: UUID): Int

    @Modifying
    @Query("DELETE from UserJobPositionReadEntity u where u.id = ?1")
    override fun deleteById(id: UUID)

    @Modifying
    @Query("DELETE from UserJobPositionReadEntity u where u.id in ?1")
    fun deleteById(ids: Collection<UUID>)

    @Query(
        """
            SELECT (count(u) > 0)
            FROM UserJobPositionReadEntity u
            WHERE   u.companyId = ?1 AND
                    u.userId = ?2 AND
                    u._status = ?3 AND
                    upper(u.dates) is null
        """
    )
    fun existsByCompanyIdAndUserIdAndStatusAndEndDateIsNull(
        companyId: UUID,
        userId: UUID,
        status: Int
    ): Boolean

    @Modifying
    @Query(
        """UPDATE read.user_job_position
           SET dates = daterange(lower(dates), cast(now() as date))
           WHERE user_id = ?1 AND company_id = ?2
       """,
        nativeQuery = true,
    )
    fun setFormerEmployee(userId: UUID, companyId: UUID): Int

    @Modifying
    @Query(
        """UPDATE read.user_job_position
           SET dates = daterange(lower(dates), cast(now() as date))
           WHERE id in ?1
       """,
        nativeQuery = true,
    )
    fun setFormerEmployee(id: Collection<UUID>): Int

    fun existsByUserIdAndCompanyId(userId: UUID, companyId: UUID): Boolean

    @Modifying
    @Query("UPDATE UserJobPositionReadEntity u SET u._status = ?2 WHERE u.id = ?1")
    fun updateStatus(id: UUID, status: Int): Int

    @Modifying
    @Query("UPDATE UserJobPositionReadEntity u SET u.isVerifyByCompany = ?3, u.verificationId = ?2 WHERE u.id = ?1")
    fun updateVerification(id: UUID, verificationId: UUID?, isVerifyByCompany: Boolean): Int
}

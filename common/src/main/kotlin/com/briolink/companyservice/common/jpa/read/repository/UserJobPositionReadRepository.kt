package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserJobPositionReadRepository : JpaRepository<UserJobPositionReadEntity, UUID> {
    fun findByCompanyIdAndUserId(companyId: UUID, userId: UUID): UserJobPositionReadEntity?

    @Modifying
    @Query(
        """update UserJobPositionReadEntity u
           set u.data = function('jsonb_sets', u.data,
                '{user,slug}', :slug, text,
                '{user,firstName}', :firstName, text,
                '{user,lastName}', :lastName, text,
                '{user,image}', :image, text
           ) where u.userId = :userId""",
    )
    fun updateUserByUserId(
        @Param("userId") userId: UUID,
        @Param("slug") slug: String?,
        @Param("firstName") firstName: String?,
        @Param("lastName") lastName: String?,
        @Param("image") image: String?,
    )

    @Query(
        """SELECT u
           FROM UserJobPositionReadEntity as u
           WHERE u.endDate is null AND companyId = ?1
        """
    )
    fun findByCompanyId(companyId: UUID, pageable: Pageable? = null): Page<UserJobPositionReadEntity>

    @Modifying
    @Query("DELETE from UserJobPositionReadEntity u where u.id = ?1")
    override fun deleteById(id: UUID)
}

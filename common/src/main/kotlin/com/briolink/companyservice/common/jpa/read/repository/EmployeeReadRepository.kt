package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.EmployeeReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface EmployeeReadRepository : JpaRepository<EmployeeReadEntity, UUID> {
    fun findByCompanyIdAndUserId(companyId: UUID, userId: UUID): EmployeeReadEntity?

    @Modifying
    @Query(
        """update EmployeeReadEntity c
           set c.data = function('jsonb_sets', c.data,
                '{user,slug}', :slug, text,
                '{user,firstName}', :firstName, text,
                '{user,lastName}', :lastName, text,
                '{user,image}', :image, text
           ) where c.userId = :userId""",
    )
    fun updateUserByUserId(
        @Param("userId") userId: UUID,
        @Param("slug") slug: String?,
        @Param("firstName") firstName: String?,
        @Param("lastName") lastName: String?,
        @Param("image") image: String?,
    )

//    @Query(
//        """SELECT e
//           FROM EmployeeReadEntity as e
//           WHERE data ->>
//        """
//    )
    fun findByCompanyId(companyId: UUID, pageable: Pageable? = null): Page<EmployeeReadEntity>

    @Query(
        """SELECT e
           FROM EmployeeReadEntity as e
           WHERE function('array_contains_element', e.userJobPositionIds, ?1) = TRUE
        """
    )
    fun findByUserJobPositionId(userJobPositionId: UUID): EmployeeReadEntity?

    fun deleteByUserIdAndCompanyId(userId: UUID, companyId: UUID): Long
}

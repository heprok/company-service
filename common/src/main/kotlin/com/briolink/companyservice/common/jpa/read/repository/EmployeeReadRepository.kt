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
        """DELETE EmployeeReadEntity e WHERE e.companyId = ?1"""
    )
    fun deleteAllByCompanyId(companyId: UUID)

    fun findByCompanyId(companyId: UUID, pageable: Pageable): Page<EmployeeReadEntity>
}

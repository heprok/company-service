package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserReadRepository : JpaRepository<UserReadEntity, UUID> {
    fun findByCompanyIdIs(userId: UUID, pageable: Pageable? = null): Page<UserReadEntity>

    @Query(
            value = """
            SELECT
                count(1)
            FROM
                UserReadEntity as u
            WHERE
                u.companyId = :company_id AND u.id = :user_id
    """,
    )
    fun isEditCompany(@Param("user_id") userId: UUID, @Param("company_id") companyId: UUID): Int
}

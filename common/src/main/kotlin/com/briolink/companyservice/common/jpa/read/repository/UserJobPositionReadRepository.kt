package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface UserJobPositionReadRepository : JpaRepository<UserJobPositionReadEntity, UUID> {
    fun findByCompanyIdIs(companyId: UUID, pageable: Pageable? = null): Page<UserJobPositionReadEntity>

//    @Query(
//            value = """
//            SELECT
//                count(1)
//            FROM
//                UserReadEntity as u
//            WHERE
//                u.companyId = :company_id AND u.id = :user_id
//    """,
//    )
    fun findByUserIdAndCompanyId(userId: UUID, companyId: UUID): Optional<UserJobPositionReadEntity>
    fun existsByUserIdAndCompanyId(userId: UUID, companyId: UUID): Boolean

}

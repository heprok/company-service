package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
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

    @Modifying
    @Query(
            """update UserJobPositionReadEntity c
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

    @Modifying
    @Query("delete from UserJobPositionReadEntity u where u.id = ?1")
    override fun deleteById(id: UUID)
}

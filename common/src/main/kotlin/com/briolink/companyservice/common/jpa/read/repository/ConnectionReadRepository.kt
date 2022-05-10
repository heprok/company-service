package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.projection.CompanyIdProjection
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

interface ConnectionReadRepository : JpaRepository<ConnectionReadEntity, UUID> {

    @Query("SELECT u FROM ConnectionReadEntity u WHERE u.fromObjectId = :fromObjectId AND u.toObjectId = :toObjectId")
    fun findByFromObjectIdAndToObjectId(
        @Param("fromObjectId") fromObjectId: UUID,
        @Param("toObjectId") toObjectId: UUID
    ): Optional<ConnectionReadEntity>

    @Modifying
    @Query("delete from ConnectionReadEntity u where u.fromObjectId = :fromObjectId AND u.toObjectId = :toObjectId")
    fun deleteBy(
        @Param("fromObjectId") fromObjectId: UUID,
        @Param("toObjectId") toObjectId: UUID
    ): Int

    @Query(
        """SELECT count(c) FROM ConnectionReadEntity c
           WHERE c.hidden = false AND
           (c.fromObjectId = :companyId AND c._toObjectType = :objectType) OR
           (c.toObjectId = :companyId AND c._fromObjectType = :objectType)
       """
    )
    fun getCountNotHiddenByCompanyAndCollabType(
        @Param("companyId") companyId: UUID,
        @Param("objectType") objectType: Int
    ): Int

    @Query(
        """
        SELECT
            distinct cast(result.company_id as varchar) as companyId
        FROM
        (
            (
                SELECT
                    from_object_id AS company_id
                FROM
                    read.connection
                WHERE from_object_type = 2
                limit :limit offset :offset
            ) union (
                SELECT
                    to_object_id AS company_id
                FROM
                    read.connection
                WHERE from_object_type = 2
                
                limit :limit offset :offset
            )
        ) as result
    """,
        nativeQuery = true
    )
    fun getAllCompanyIds(
        @Param("limit") limit: Int = 10,
        @Param("offset") offset: Int = 0
    ): List<CompanyIdProjection>
}

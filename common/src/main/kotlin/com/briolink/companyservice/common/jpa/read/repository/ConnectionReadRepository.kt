package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.enumeration.ProjectStageEnum
import com.briolink.companyservice.common.jpa.projection.CompanyIdProjection
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

interface ConnectionReadRepository : JpaRepository<ConnectionReadEntity, UUID> {

    @Query(
        """
        SELECT c from ConnectionReadEntity c
        where
            (c.participantToCompanyId = ?1 or c.participantFromCompanyId = ?1) AND
            c._status = ?2 AND (
                function('array_contains_element', c.hiddenCompanyIds, ?1) = FALSE AND
                function('array_contains_element', c.deletedCompanyIds, ?1) = FALSE
            )
    """,
    )
    fun getByCompanyIdAndStatusAndNotHiddenOrNotDeleted(
        companyId: UUID,
        type: Int = ProjectStageEnum.Verified.value
    ): List<ConnectionReadEntity>

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
        """
        SELECT
            cast(result.company_id as varchar) as companyId
        FROM
        (
            (
                SELECT
                    participant_from_company_id AS company_id
                FROM
                    read.connection
                where
                    participant_to_company_id = cast(:companyId as uuid)
                limit :limit offset :offset
            ) union (
                SELECT
                    participant_to_company_id AS company_id
                FROM
                    read.connection
                where
                    participant_from_company_id = cast(:companyId as uuid)
                limit :limit offset :offset
            )
        ) as result
    """,
        nativeQuery = true
    )
    fun getCompanyIdsByCompanyId(
        @Param("companyId") companyId: String,
        @Param("limit") limit: Int = 10,
        @Param("offset") offset: Int = 0
    ): List<CompanyIdProjection>
}

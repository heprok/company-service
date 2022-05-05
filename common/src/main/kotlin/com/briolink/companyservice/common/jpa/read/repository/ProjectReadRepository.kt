package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.projection.CompanyIdProjection
import com.briolink.companyservice.common.jpa.read.entity.ProjectReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ProjectReadRepository : JpaRepository<ProjectReadEntity, UUID> {
    @Modifying
    @Query("delete from ProjectReadEntity c where c.id = ?1")
    fun deleteByIds(id: UUID): Int

    @Modifying
    @Query(
        """
           UPDATE ProjectReadEntity c
           SET c.deletedCompanyIds = function('array_append', c.deletedCompanyIds, :companyId)
           WHERE
               c.id = :id AND
               function('array_contains_element', c.deletedCompanyIds, :companyId) = false
               """,
    )
    fun softDeleteCompanyIdById(
        @Param("id") id: UUID,
        @Param("companyId") companyId: UUID
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
                    read.project
                where
                    participant_to_company_id = cast(:companyId as uuid)
                limit :limit offset :offset
            ) union (
                SELECT
                    participant_to_company_id AS company_id
                FROM
                    read.project
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

    @Modifying
    @Query(
        """
            UPDATE ProjectReadEntity c
            SET c.hiddenCompanyIds = CASE
               WHEN :hidden = true
                   THEN function('array_append', c.hiddenCompanyIds, :companyId)
               WHEN :hidden = false
                   THEN function('array_remove', c.hiddenCompanyIds, :companyId)
               ELSE c.hiddenCompanyIds END
           WHERE
               id = :id AND
               function('array_contains_element', c.hiddenCompanyIds, :companyId) <> :hidden
           """,
    )
    fun changeVisibilityById(
        @Param("id") id: UUID,
        @Param("companyId") companyId: UUID,
        @Param("hidden") hidden: Boolean
    ): Int

    @Query(
        """
        SELECT c from ProjectReadEntity c
        WHERE
            (c.participantToCompanyId = ?1 or c.participantFromCompanyId = ?1) AND
            function('array_contains_element', c.hiddenCompanyIds, ?1) = FALSE AND
            function('array_contains_element', c.acceptedCompanyIds, ?1) = TRUE AND
            function('array_contains_element', c.deletedCompanyIds, ?1) = FALSE

    """,
    )
    fun findNotHiddenByCompanyId(
        companyId: UUID,
        pageable: Pageable = PageRequest.ofSize(1000)
    ): Page<ProjectReadEntity>

    @Modifying
    @Query(
        """
        UPDATE ProjectReadEntity p
        SET p.data = function('jsonb_sets', p.data,
                '{projectService,service,slug}', :slug, text
                )
        WHERE p.serviceId = :serviceId
        """
    )
    fun updateServiceSlug(@Param("serviceId") serviceId: String, @Param("slug") slug: String): Int
}

package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.ProjectReadEntity
import org.springframework.data.domain.Page
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
        SELECT c from ConnectionReadEntity c
        where
            (c.participantToCompanyId = ?1 or c.participantFromCompanyId = ?1)
             AND (
                function('array_contains_element', c.hiddenCompanyIds, ?1) = FALSE
            )
    """,
    )
    fun findNotHiddenByCompanyId(
        companyId: UUID,
        pageable: Pageable = Pageable.ofSize(1000)
    ): Page<ProjectReadEntity>

    fun findNotHiddenByCompanyId
}

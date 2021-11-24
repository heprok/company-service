package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.enumration.ConnectionStatusEnum
import com.briolink.companyservice.common.jpa.projection.IdNameProjection
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ConnectionReadRepository : JpaRepository<ConnectionReadEntity, UUID> {

    @Query(
        """
        select c from ConnectionReadEntity c
        where 
            (c.participantToCompanyId = ?1 or c.participantFromCompanyId = ?1) and
            c._status = ?2 and (
                function('array_contains_element', c.hiddenCompanyIds, ?1) = FALSE and
                function('array_contains_element', c.deletedCompanyIds, ?1) = FALSE
            )
    """,
    )
    fun getByCompanyIdAndStatusAndNotHiddenOrNotDeleted(
        companyId: UUID,
        type: Int = ConnectionStatusEnum.Verified.value
    ): List<ConnectionReadEntity> // ktlint-disable max-line-length

    @Modifying
    @Query("delete from ConnectionReadEntity c where c.id = ?1")
    override fun deleteById(id: UUID)

    @Query(
        """
        SELECT cast(company.id as varchar), company.name FROM read.company as company
            WHERE
                (:query is null or company.name @@to_tsquery( quote_literal( quote_literal( :query ) ) || ':*' ) = true) 
                AND EXISTS (
                SELECT 1 FROM
                    read.connection as connection
                WHERE
                    ( connection.participant_to_company_id = cast(:companyId as uuid) AND connection.participant_from_company_id != company.id ) 
                    OR ( connection.participant_from_company_id = cast(:companyId as uuid) AND connection.participant_to_company_id = company.id ) 
                    LIMIT 1
                ) LIMIT 10
    """,
        nativeQuery = true,
    )
    fun getCollaboratorsByCompanyId(@Param("companyId") companyId: String, @Param("query") query: String?): List<IdNameProjection>

    @Query(
        """
        SELECT cast(connection_service.id as varchar), connection_service.name FROM read.connection_service as connection_service
            WHERE
                (:query is null or connection_service.name @@to_tsquery( quote_literal( quote_literal( :query ) ) || ':*' ) = true) 
                AND EXISTS (
                SELECT 1 FROM
                    read.connection as connection
                WHERE
                    (
                    (connection.participant_to_company_id = cast(:companyId as uuid) OR connection.participant_from_company_id = cast(:companyId as uuid))
                    AND connection.service_ids @> ARRAY[connection_service.id] )
                    LIMIT 1
                ) LIMIT 10
    """,
        nativeQuery = true,
    )
    fun getConnectionServicesByCompanyId(@Param("companyId") companyId: String, @Param("query") query: String?): List<IdNameProjection>

    @Query(
        """
        SELECT cast(company_industry.id as varchar), company_industry.name FROM read.industry as company_industry
            WHERE
                (:query is null or company_industry.name @@to_tsquery( quote_literal( quote_literal( :query ) ) || ':*' ) = true) 
                AND EXISTS (
                SELECT 1 FROM
                    read.connection as connection
                WHERE
                    (
                    (connection.participant_to_company_id = cast(:companyId as uuid) OR connection.participant_from_company_id = cast(:companyId as uuid))
                    AND connection.company_industry_id = company_industry.id)
                    LIMIT 1
                ) LIMIT 10
    """,
        nativeQuery = true,
    )
    fun getConnectionIndustriesByCompanyId(@Param("companyId") companyId: String, @Param("query") query: String?): List<IdNameProjection>

    @Modifying
    @Query(
        """
            UPDATE ConnectionReadEntity c
            SET c.hiddenCompanyIds = case
               when :hidden = true
                   then function('array_append', c.hiddenCompanyIds, :companyId)
               when :hidden = false
                   then function('array_remove', c.hiddenCompanyIds, :companyId)
               else c.hiddenCompanyIds end
           WHERE
               c.id = :id and
               (c.participantFromCompanyId = :companyId or c.participantToCompanyId = :companyId) and
               function('array_contains_element', c.hiddenCompanyIds, :companyId) <> :hidden
           """,
    )
    fun changeVisibilityByIdAndCompanyId(
        @Param("id") connectionId: UUID,
        @Param("companyId") companyId: UUID,
        @Param("hidden") hidden: Boolean
    )

    @Modifying
    @Query(
        """
            UPDATE ConnectionReadEntity c
            SET c.hiddenCompanyIds = case
               when :hidden = true
                   then function('array_append', c.hiddenCompanyIds, :companyId)
               when :hidden = false
                   then function('array_remove', c.hiddenCompanyIds, :companyId)
               else c.hiddenCompanyIds end
           WHERE
               (c.participantFromCompanyId = :companyId OR c.participantToCompanyId = :companyId) AND
               (c.participantFromUserId = :userId OR c.participantToUserId = :userId) AND
               function('array_contains_element', c.hiddenCompanyIds, :companyId) <> :hidden
           """,
    )

    fun changeVisibilityByCompanyIdAndUserId(
        @Param("companyId") companyId: UUID,
        @Param("userId") userId: UUID,
        @Param("hidden") hidden: Boolean
    )

    fun existsByParticipantFromCompanyIdOrParticipantToCompanyId(participantFromCompanyId: UUID, participantToCompanyId: UUID): Boolean

    @Modifying
    @Query(
        """
           UPDATE ConnectionReadEntity c
           SET c.deletedCompanyIds = function('array_append', c.deletedCompanyIds, :companyId)
           WHERE
               c.id = :id and
               (c.participantFromCompanyId = :companyId or c.participantToCompanyId = :companyId) and
               function('array_contains_element', c.deletedCompanyIds, :companyId) = false
               """,
    )
    fun softDeleteByIdAndCompanyId(
        @Param("id") id: UUID,
        @Param("companyId") companyId: UUID
    )
}

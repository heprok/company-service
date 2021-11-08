package com.briolink.companyservice.common.jpa.read.repository.connection

import com.briolink.companyservice.common.jpa.projection.CollaboratorRoleProjection
import com.briolink.companyservice.common.jpa.projection.IndustryProjection
import com.briolink.companyservice.common.jpa.projection.CollaboratorProjection
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface ConnectionReadRepository : JpaRepository<ConnectionReadEntity, UUID>, JpaSpecificationExecutor<ConnectionReadEntity> {
//    fun findByBuyerIdIs(companyId: UUID, pageable: Pageable? = null): Page<ConnectionReadEntity>

    fun findBySellerIdOrBuyerId(
        sellerId: UUID,
        buyerId: UUID
    ): List<ConnectionReadEntity>


    fun existsBySellerIdAndBuyerId(sellerId: UUID, buyerId: UUID): Boolean

    fun findBySellerIdOrBuyerIdAndVerificationStage(
        sellerRoleId: UUID,
        buyerRoleId: UUID,
        verificationStage: ConnectionReadEntity.ConnectionStatus
    ): List<ConnectionReadEntity>

    @Query(
            value = """
            SELECT 
                distinct c.industryId as id, c.industryName  as name
            FROM 
                ConnectionReadEntity c
            WHERE 
                (sellerId = :companyId OR buyerId = :companyId) AND
                industryName LIKE %:query%
        """,
    )
    fun getIndustriesUsesCompany(
        @Param("query") query: String,
        @Param("companyId") companyId: UUID,
    ): List<IndustryProjection>

    @Query(
            value = """
            SELECT 
                distinct c.buyerId as id, c.buyerName as name
            FROM 
                ConnectionReadEntity c
            WHERE 
                sellerId = :companyId AND
                buyerName LIKE %:query%
        """,
    )
    fun getCollaboratorsBuyerUsedForCompany(
        @Param("query") query: String,
        @Param("companyId") sellerCompanyId: UUID,
    ): List<CollaboratorProjection>

    @Query(
            value = """
            SELECT 
                distinct c.sellerId as id, c.sellerName as name
            FROM 
                ConnectionReadEntity c
            WHERE 
                buyerId = :companyId AND
                sellerName LIKE %:query%
        """,
    )
    fun getCollaboratorsSellerUsedForCompany(
        @Param("query") query: String,
        @Param("companyId") buyerCompanyId: UUID,
    ): List<CollaboratorProjection>

    @Query(
            value = """
            SELECT 
                distinct c.buyerRoleName, c.buyerRoleId
            FROM 
                ConnectionReadEntity c
            WHERE 
                sellerId = :companyId AND
                buyerRoleName LIKE %:query%
        """,
    )
    fun getCollaboratorsRolesBuyerUsedForCompany(
        @Param("query") query: String,
        @Param("companyId") sellerCompanyId: UUID,
    ): List<CollaboratorRoleProjection>

    @Query(
            value = """
            SELECT 
                distinct c.sellerId as id, c.sellerName as name
            FROM 
                ConnectionReadEntity c
            WHERE 
                buyerId = :companyId AND
                sellerRoleName LIKE %:query%
        """,
    )
    fun getCollaboratorsSellerRolesUsedForCompany(
        @Param("query") query: String,
        @Param("companyId") buyerCompanyId: UUID,
    ): List<CollaboratorRoleProjection>
}

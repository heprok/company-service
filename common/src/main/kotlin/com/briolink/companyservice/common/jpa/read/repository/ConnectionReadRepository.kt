package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface ConnectionReadRepository : JpaRepository<ConnectionReadEntity, UUID>, JpaSpecificationExecutor<ConnectionReadEntity> {
//    fun findByBuyerIdIs(companyId: UUID, pageable: Pageable? = null): Page<ConnectionReadEntity>

    fun findBySellerIdOrBuyerId(
        sellerId: UUID,
        buyerId: UUID
    ): List<ConnectionReadEntity>


    fun existsBySellerIdAndBuyerId(sellerId: UUID, buyerId: UUID): Boolean

}

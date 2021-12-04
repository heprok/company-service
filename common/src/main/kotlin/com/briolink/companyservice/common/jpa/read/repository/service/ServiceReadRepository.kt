package com.briolink.companyservice.common.jpa.read.repository.service

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ServiceReadRepository : JpaRepository<ServiceReadEntity, UUID>, JpaSpecificationExecutor<ServiceReadEntity> {
    fun findByCompanyIdIs(companyId: UUID, pageable: Pageable? = null): Page<ServiceReadEntity>

    fun existsByCompanyId(companyId: UUID): Boolean

    @Modifying
    @Query(
        """
        UPDATE read.service SET is_hidden = NOT is_hidden where id = ?1 and company_id = ?2
    """,
        nativeQuery = true,
    )
    fun toggleVisibilityByIdAndCompanyId(serviceId: UUID, companyId: UUID)

    @Modifying
    @Query(
        """UPDATE ServiceReadEntity s 
            SET s.verifiedUses = (SELECT count(cs.serviceId) FROM ConnectionServiceReadEntity cs WHERE serviceId = ?1 AND cs.hidden = false)
            WHERE s.id = ?1
        """
    )
    fun refreshVerifyUses(serviceId: UUID)

    @Modifying
    @Query("DELETE from ServiceReadEntity c where c.id = ?1")
    override fun deleteById(id: UUID)
}

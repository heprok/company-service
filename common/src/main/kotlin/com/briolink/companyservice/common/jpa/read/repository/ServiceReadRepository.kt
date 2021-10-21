package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ServiceReadRepository : JpaRepository<ServiceReadEntity, UUID>, JpaSpecificationExecutor<ServiceReadEntity> {
    fun findByCompanyIdIs(companyId: UUID, pageable: Pageable? = null): Page<ServiceReadEntity>

    fun existsByCompanyId(companyId: UUID): Boolean

}

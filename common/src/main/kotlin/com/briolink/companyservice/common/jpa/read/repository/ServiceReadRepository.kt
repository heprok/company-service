package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ServiceReadRepository : JpaRepository<ServiceReadEntity, UUID>, JpaSpecificationExecutor<ServiceReadEntity> {
    fun findByCompanyIdIs(companyId: UUID, pageable: Pageable? = null): Page<ServiceReadEntity>

    fun existsByCompanyId(companyId: UUID): Boolean

    @Modifying
    @Query("UPDATE ServiceReadEntity s SET s.isHide = ?3 where s.id = ?1 and s.companyId = ?2")
    fun hideServiceByIdAndCompanyId(id: UUID, companyId: UUID, isHide: Boolean)

}

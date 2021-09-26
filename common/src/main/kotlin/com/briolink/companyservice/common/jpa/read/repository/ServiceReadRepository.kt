package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.UUID

interface ServiceReadRepository : JpaRepository<ServiceReadEntity, UUID>, QuerydslPredicateExecutor<ServiceReadEntity>
{
    fun findByCompanyIdIs(companyId: UUID, pageable: Pageable? = null): Page<ServiceReadEntity>
}

package com.briolink.companyservice.common.jpa.read.repository;

import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ConnectionReadRepository : JpaRepository<ConnectionReadEntity, String> {
    fun findByParticipantToCompanyIdIs(companyId: UUID, pageable: Pageable? = null): Page<ConnectionReadEntity>

}

package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ConnectionServiceReadRepository : JpaRepository<ConnectionServiceReadEntity, UUID> {
    fun existsByIdAndCompanyIdAndServiceId(id: UUID, companyId: UUID, serviceId: UUID): Boolean
}

package com.briolink.companyservice.common.jpa.read.repository.connection.service

import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface ConnectionServiceReadRepository : JpaRepository<ConnectionServiceReadEntity, UUID>,
    JpaSpecificationExecutor<ConnectionServiceReadEntity> {
    fun existsByConnectionIdAndCompanyIdAndServiceId(connectionId: UUID, companyId: UUID, serviceId: UUID): Boolean
}

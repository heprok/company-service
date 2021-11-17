package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.projection.IdNameProjection
import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface ConnectionServiceReadRepository : JpaRepository<ConnectionServiceReadEntity, UUID> {
    fun existsByIdAndCompanyIdAndServiceId(id: UUID, companyId: UUID, serviceId: UUID): Boolean
}

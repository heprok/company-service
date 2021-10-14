package com.briolink.companyservice.common.jpa.read.repository;

import com.briolink.companyservice.common.jpa.read.entity.StatisticReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface StatisticReadRepository : JpaRepository<StatisticReadEntity, UUID> {
    fun findByCompanyId(companyId: UUID): Optional<StatisticReadEntity>
}

package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.statistic.StatisticReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface StatisticReadRepository : JpaRepository<StatisticReadEntity, UUID> {
    fun findByCompanyId(companyId: UUID): StatisticReadEntity?
    @Modifying
    @Query("DELETE from StatisticReadEntity s where s.companyId = ?1")
    fun deleteByCompanyId(serviceId: UUID)
}

package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.OccupationWriteEntity
import com.briolink.lib.sync.BaseTimeMarkRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface OccupationWriteRepository : JpaRepository<OccupationWriteEntity, UUID>, BaseTimeMarkRepository<OccupationWriteEntity> {
    @Query("SELECT c FROM OccupationWriteEntity c WHERE lower(c.name) = lower(?1)")
    fun findByName(name: String): OccupationWriteEntity?

    @Query("SELECT c from OccupationWriteEntity c WHERE c.created BETWEEN ?1 AND ?2 OR c.changed BETWEEN ?1 AND ?2")
    override fun findByPeriod(start: Instant, end: Instant, pageable: Pageable): Page<OccupationWriteEntity>
}

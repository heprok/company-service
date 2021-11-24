package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.OccupationWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface OccupationWriteRepository : JpaRepository<OccupationWriteEntity, UUID> {
    @Query("SELECT c FROM OccupationWriteEntity c WHERE lower(c.name) = lower(?1)")
    fun findByName(name: String): OccupationWriteEntity?
}

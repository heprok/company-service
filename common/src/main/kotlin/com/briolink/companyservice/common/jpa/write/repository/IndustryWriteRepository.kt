package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.IndustryWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface IndustryWriteRepository : JpaRepository<IndustryWriteEntity, UUID> {
    @Query("SELECT c FROM IndustryWriteEntity c WHERE lower(c.name) = lower(?1)")
    fun findByName(name: String): IndustryWriteEntity?
}

package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.KeywordWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface KeywordWriteRepository : JpaRepository<KeywordWriteEntity, UUID>{
    @Query("SELECT c FROM KeywordWriteEntity c WHERE lower(c.name) = lower(?1)")
    fun findByName(name: String): KeywordWriteEntity?
}

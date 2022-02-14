package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.KeywordWriteEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface KeywordWriteRepository : JpaRepository<KeywordWriteEntity, UUID> {
    @Query("SELECT c FROM KeywordWriteEntity c WHERE lower(c.name) = lower(?1)")
    fun findByName(name: String): KeywordWriteEntity?

    @Query("SELECT c from KeywordWriteEntity c WHERE c.created BETWEEN ?1 AND ?2 OR c.changed BETWEEN ?1 AND ?2")
    fun findByCreatedOrChangedBetween(
        start: Instant,
        end: Instant,
        pageable: Pageable
    ): Page<KeywordWriteEntity>
}

package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface CompanyWriteRepository : JpaRepository<CompanyWriteEntity, UUID> {
    @Query("SELECT count(c.id) > 0 FROM CompanyWriteEntity c WHERE lower(c.website) = lower(?1)")
    fun existsByWebsite(website: String): Boolean

    @Query("SELECT c FROM CompanyWriteEntity c WHERE lower(c.website) = lower(?1)")
    fun getByWebsite(website: String): CompanyWriteEntity?

    @Query("SELECT count(c) > 0 FROM CompanyWriteEntity c WHERE lower(c.name) = lower(?1)")
    fun existsByName(name: String): Boolean

    fun getByNameIgnoreCaseAndWebsiteIgnoreCase(name: String, website: String?): CompanyWriteEntity?

    @Query("SELECT c from CompanyWriteEntity c WHERE c.created BETWEEN ?1 AND ?2 OR c.changed BETWEEN ?1 AND ?2")
    fun findByCreatedOrChangedBetween(
        start: Instant,
        end: Instant,
        pageable: Pageable
    ): Page<CompanyWriteEntity>
}

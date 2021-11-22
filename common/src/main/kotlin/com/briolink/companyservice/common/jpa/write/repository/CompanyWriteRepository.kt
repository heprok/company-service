package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface CompanyWriteRepository : JpaRepository<CompanyWriteEntity, UUID> {
    @Query("SELECT count(1) > 0 FROM CompanyWriteEntity c WHERE lower(c.website) = lower(?1)")
    fun existsByWebsite(website: String): Boolean

    @Query("SELECT c FROM CompanyWriteEntity c WHERE lower(c.name) = lower(?1) OR lower(c.website) = lower(?2)")
    fun getByNameOrWebsite(name: String, website: String): CompanyWriteEntity?

    @Query("SELECT c FROM CompanyWriteEntity c WHERE lower(c.name) = lower(?1)")
    fun getByName(name: String): CompanyWriteEntity?

}

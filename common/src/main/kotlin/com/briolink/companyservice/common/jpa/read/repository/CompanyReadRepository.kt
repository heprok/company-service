package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface CompanyReadRepository : JpaRepository<CompanyReadEntity, UUID> {
    fun findBySlug(slug: String): CompanyReadEntity
    fun findByIdIsIn(ids: List<UUID>): List<CompanyReadEntity>
    @Query("SELECT id FROM CompanyReadEntity")
    fun getAllCompanyUUID(): List<UUID>
}

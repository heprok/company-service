package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface IndustryReadRepository : JpaRepository<IndustryReadEntity, UUID> {
    @Query("SELECT c FROM IndustryReadEntity c WHERE (:query is null or function('fts_partial', c.name, :query) = true)")
    fun findByName(@Param("query") query: String?, pageable: Pageable = Pageable.ofSize(10)): List<IndustryReadEntity>

    fun existsByName(name: String): Boolean

    @Query("SELECT c FROM IndustryReadEntity c WHERE c.name = ?1")
    fun findByNameOrNull(name: String): IndustryReadEntity?
}

package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.KeywordReadEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface KeywordReadRepository : JpaRepository<KeywordReadEntity, UUID> {
    @Query("SELECT c FROM KeywordReadEntity c WHERE (:query is null or function('fts_partial', c.name, :query) = true)")
    fun findByName(@Param("query") query: String?, pageable: Pageable = Pageable.ofSize(10)): List<KeywordReadEntity>

    @Query("SELECT c FROM KeywordReadEntity c WHERE c.name = ?1")
    fun findByNameOrNull(name: String): KeywordReadEntity?
}

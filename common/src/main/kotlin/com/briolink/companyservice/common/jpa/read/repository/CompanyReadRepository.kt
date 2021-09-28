package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.UUID

interface CompanyReadRepository : JpaRepository<CompanyReadEntity, UUID>, QuerydslPredicateExecutor<CompanyReadEntity> {


    fun findBySlug(slug: String): CompanyReadEntity

}

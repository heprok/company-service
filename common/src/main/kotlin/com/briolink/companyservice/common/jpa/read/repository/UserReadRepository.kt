package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.UUID

interface UserReadRepository : JpaRepository<UserReadEntity, UUID>, QuerydslPredicateExecutor<UserReadEntity>
{
    fun findByCompanyIdIs(userId: UUID, pageable: Pageable? = null): Page<UserReadEntity>
}

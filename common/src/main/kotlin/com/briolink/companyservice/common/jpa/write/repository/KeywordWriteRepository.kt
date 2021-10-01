package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.KeywordWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.UUID

interface KeywordWriteRepository : JpaRepository<KeywordWriteEntity, UUID>, QuerydslPredicateExecutor<KeywordWriteEntity>

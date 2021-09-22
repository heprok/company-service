package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.KeyWordWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.UUID

interface KeyWordWriteRepository : JpaRepository<KeyWordWriteEntity, UUID>, QuerydslPredicateExecutor<KeyWordWriteEntity>

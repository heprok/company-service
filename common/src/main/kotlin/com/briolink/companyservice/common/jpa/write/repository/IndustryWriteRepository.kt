package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.IndustryWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.UUID

interface IndustryWriteRepository : JpaRepository<IndustryWriteEntity, UUID>, QuerydslPredicateExecutor<IndustryWriteEntity>

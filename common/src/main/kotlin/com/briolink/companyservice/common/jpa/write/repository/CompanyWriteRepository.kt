package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CompanyWriteRepository : JpaRepository<CompanyWriteEntity, UUID>

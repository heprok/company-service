package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.write.entity.UserWriteEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserWriteRepository : JpaRepository<UserWriteEntity, UUID>
{
    fun findByCompanyIdIs(companyId: UUID, pageable: Pageable? = null): Page<UserReadEntity>
}

package com.briolink.companyservice.common.jpa.write.repository

import com.briolink.companyservice.common.jpa.write.entity.EventStoreWriteEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface EventStoreWriteRepository : JpaRepository<EventStoreWriteEntity, UUID>

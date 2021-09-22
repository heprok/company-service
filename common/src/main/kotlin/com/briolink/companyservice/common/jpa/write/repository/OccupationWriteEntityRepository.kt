package com.briolink.companyservice.common.jpa.write.repository;

import com.briolink.companyservice.common.jpa.write.entity.OccupationWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OccupationWriteEntityRepository : JpaRepository<OccupationWriteEntity, UUID> {
}

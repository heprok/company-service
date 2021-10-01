package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OccupationReadRepository : JpaRepository<OccupationReadEntity, UUID> {
}

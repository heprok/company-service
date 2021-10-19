package com.briolink.companyservice.common.jpa.read.repository;

import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserReadRepository : JpaRepository<UserReadEntity, UUID> {
}

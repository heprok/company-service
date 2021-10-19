package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserJobPositionService(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
) {
    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<UserJobPositionReadEntity> =
            userJobPositionReadRepository.findByCompanyIdIs(id, PageRequest(offset, limit))
}

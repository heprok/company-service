package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userReadRepository: UserReadRepository,
) {
    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<UserReadEntity> =
            userReadRepository.findByCompanyIdIs(id, PageRequest(offset, limit))
}

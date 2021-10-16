package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ConnectionService(
    private val connectionReadRepository: ConnectionReadRepository,
) {
    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<ConnectionReadEntity> =
            connectionReadRepository.findByBuyerIdIs(id, PageRequest(offset, limit))
}

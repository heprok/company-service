package com.briolink.companyservice.api.service.service

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ServiceQueryService(private val serviceReadRepository: ServiceReadRepository) {
    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<ServiceReadEntity> =
        serviceReadRepository.findByCompanyIdIs(id, PageRequest(offset, limit))
}

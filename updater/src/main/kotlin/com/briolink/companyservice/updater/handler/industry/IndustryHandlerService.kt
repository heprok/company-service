package com.briolink.companyservice.updater.handler.industry

import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.read.repository.IndustryReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class IndustryHandlerService(
    private val industryReadRepository: IndustryReadRepository
) {
    fun create(industry: Industry) {
        industryReadRepository.save(
                IndustryReadEntity(id = industry.id, name = industry.name),
        )
    }

}


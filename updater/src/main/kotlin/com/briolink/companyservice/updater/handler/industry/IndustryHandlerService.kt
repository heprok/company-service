package com.briolink.companyservice.updater.handler.industry

import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.read.repository.IndustryReadRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class IndustryHandlerService(
    private val industryReadRepository: IndustryReadRepository
) {
    fun createOrUpdate(
        entityPrevIndustry: IndustryReadEntity? = null,
        industryEventData: Industry
    ): IndustryReadEntity {

        val industry = entityPrevIndustry ?: industryReadRepository.findByNameOrNull(industryEventData.name) ?: IndustryReadEntity(
            industryEventData.id,
            industryEventData.name,
        )
        return industryReadRepository.save(industry)
    }

    fun findById(id: UUID) = industryReadRepository.findByIdOrNull(id)
}

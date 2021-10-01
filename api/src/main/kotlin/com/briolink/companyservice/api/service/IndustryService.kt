package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.write.entity.IndustryWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.IndustryWriteRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class IndustryService(
    private val industryWriteRepository: IndustryWriteRepository
) {
    fun createIndustry(industry: IndustryWriteEntity): IndustryWriteEntity = industryWriteRepository.save(industry)
    fun findById(id: UUID) = industryWriteRepository.findById(id)
}

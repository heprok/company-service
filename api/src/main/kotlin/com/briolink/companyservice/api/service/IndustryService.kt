package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.event.v1_0.IndustryCreatedEvent
import com.briolink.companyservice.common.jpa.write.entity.IndustryWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.IndustryWriteRepository
import com.briolink.companyservice.common.mapper.IndustryMapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class IndustryService(
    private val industryWriteRepository: IndustryWriteRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    val mapper = IndustryMapper.INSTANCE

    fun create(name: String) = IndustryWriteEntity().apply {
        this.name = name
        industryWriteRepository.save(this)
        applicationEventPublisher.publishEvent(IndustryCreatedEvent(mapper.toDomain(this)))
    }

    fun findById(id: UUID) = industryWriteRepository.findById(id)
}

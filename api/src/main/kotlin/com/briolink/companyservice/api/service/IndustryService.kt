package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.companyservice.common.event.v1_0.IndustryCreatedEvent
import com.briolink.companyservice.common.jpa.write.entity.IndustryWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.IndustryWriteRepository
import com.briolink.event.publisher.EventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class IndustryService(
    private val industryWriteRepository: IndustryWriteRepository,
    private val eventPublisher: EventPublisher,
) {

    fun create(name: String): IndustryWriteEntity {
        val industry = industryWriteRepository.save(
                IndustryWriteEntity().apply {
                    this.name = name
                },
        )
        val domain = Industry(id = industry.id!!, name = industry.name)
        eventPublisher.publishAsync(IndustryCreatedEvent(domain))
        return industry
    }

    fun create(entity: IndustryWriteEntity) = IndustryWriteEntity().apply {
        name = entity.name
        industryWriteRepository.save(this).let {
            eventPublisher.publishAsync(IndustryCreatedEvent(it.toDomain()))
        }
    }

    fun findById(id: UUID) = industryWriteRepository.findById(id)
}

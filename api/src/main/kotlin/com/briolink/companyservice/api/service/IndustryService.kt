package com.briolink.companyservice.api.service

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
    fun create(name: String): IndustryWriteEntity =
            IndustryWriteEntity().apply {
                this.name = name
                industryWriteRepository.save(this).let {
                    eventPublisher.publish(IndustryCreatedEvent(it.toDomain()))
                }
            }

    fun findById(id: UUID) = industryWriteRepository.findById(id)
}

package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.event.v1_0.OccupationCreatedEvent
import com.briolink.companyservice.common.jpa.write.entity.OccupationWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.OccupationWriteRepository
import com.briolink.companyservice.common.mapper.OccupationMapper
import com.briolink.event.publisher.EventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class OccupationService(
    private val occupationWriteRepository: OccupationWriteRepository,
    private val eventPublisher: EventPublisher,
) {
    val mapper = OccupationMapper.INSTANCE

    fun create(name: String): OccupationWriteEntity =
            occupationWriteRepository.findByName(name) ?: OccupationWriteEntity().apply {
                this.name = name
                occupationWriteRepository.save(this).let {
                    eventPublisher.publishAsync(OccupationCreatedEvent(it.toDomain()))
                }
            }

    fun findById(id: UUID) = occupationWriteRepository.findById(id)
}

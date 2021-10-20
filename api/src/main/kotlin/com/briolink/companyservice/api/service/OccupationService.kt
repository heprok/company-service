package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.event.v1_0.KeywordCreatedEvent
import com.briolink.companyservice.common.event.v1_0.OccupationCreatedEvent
import com.briolink.companyservice.common.jpa.write.entity.KeywordWriteEntity
import com.briolink.companyservice.common.jpa.write.entity.OccupationWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.OccupationWriteRepository
import com.briolink.companyservice.common.mapper.OccupationMapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OccupationService(
    private val occupationWriteRepository: OccupationWriteRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    val mapper = OccupationMapper.INSTANCE

    fun create(name: String) = OccupationWriteEntity().apply {
        this.name = name
        occupationWriteRepository.save(this)
        applicationEventPublisher.publishEvent(OccupationCreatedEvent(mapper.toDomain(this)))
    }

    fun create(entity: OccupationWriteEntity) = OccupationWriteEntity().apply {
        this.id = entity.id
        this.name = entity.name
        occupationWriteRepository.save(this)
        applicationEventPublisher.publishEvent(OccupationCreatedEvent(mapper.toDomain(this)))
    }

    fun findById(id: UUID) = occupationWriteRepository.findById(id)
}

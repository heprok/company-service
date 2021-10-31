package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.event.v1_0.KeywordCreatedEvent
import com.briolink.companyservice.common.jpa.write.entity.KeywordWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.KeywordWriteRepository
import com.briolink.companyservice.common.mapper.KeywordMapper
import com.briolink.event.publisher.EventPublisher
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class KeywordService(
    private val keywordWriteRepository: KeywordWriteRepository,
    private val eventPublisher: EventPublisher,
) {
    val mapper = KeywordMapper.INSTANCE

    fun create(name: String) = KeywordWriteEntity().apply {
        this.name = name
        keywordWriteRepository.save(this)
        eventPublisher.publishAsync(KeywordCreatedEvent(mapper.toDomain(this)))
    }

    fun create(entity: KeywordWriteEntity) = KeywordWriteEntity().apply {
        this.id = entity.id
        this.name = entity.name
        keywordWriteRepository.save(this)
        eventPublisher.publishAsync(KeywordCreatedEvent(mapper.toDomain(this)))
    }

    fun findById(id: UUID) = keywordWriteRepository.findById(id)
}

package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.domain.v1_0.KeywordSyncData
import com.briolink.companyservice.common.event.v1_0.KeywordCreatedEvent
import com.briolink.companyservice.common.event.v1_0.KeywordSyncEvent
import com.briolink.companyservice.common.jpa.write.entity.KeywordWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.KeywordWriteRepository
import com.briolink.companyservice.common.mapper.KeywordMapper
import com.briolink.event.publisher.EventPublisher
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.model.PeriodDateTime
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class KeywordService(
    private val keywordWriteRepository: KeywordWriteRepository,
    private val eventPublisher: EventPublisher,
) {
    val mapper = KeywordMapper.INSTANCE

    fun create(name: String): KeywordWriteEntity {
        return keywordWriteRepository.findByName(name) ?: KeywordWriteEntity().apply {
            this.name = name
            keywordWriteRepository.save(this).let {
                eventPublisher.publishAsync(KeywordCreatedEvent(mapper.toDomain(it)))
            }
        }
    }

    fun create(entity: KeywordWriteEntity) = KeywordWriteEntity().apply {
        this.id = entity.id
        this.name = entity.name
        keywordWriteRepository.save(this)
        eventPublisher.publishAsync(KeywordCreatedEvent(mapper.toDomain(this)))
    }

    fun findById(id: UUID) = keywordWriteRepository.findById(id)

    @Async
    fun publishSyncEvent(syncId: Int, period: PeriodDateTime? = null) {
        var pageRequest = PageRequest.of(0, 200)
        var page = if (period == null) keywordWriteRepository.findAll(pageRequest)
        else keywordWriteRepository.findByCreatedOrChangedBetween(period.startInstants, period.endInstant, pageRequest)
        var indexRow = 0
        while (!page.isEmpty) {
            pageRequest = pageRequest.next()
            page.content.forEach {
                indexRow += 1
                eventPublisher.publish(
                    KeywordSyncEvent(
                        KeywordSyncData(
                            service = ServiceEnum.Company,
                            indexObjectSync = indexRow.toLong(),
                            totalObjectSync = page.totalElements,
                            syncId = syncId,
                            objectSync = it.toDomain()
                        )
                    )
                )
            }
            page = if (period == null) keywordWriteRepository.findAll(pageRequest)
            else keywordWriteRepository.findByCreatedOrChangedBetween(
                period.startInstants,
                period.endInstant,
                pageRequest
            )
        }
    }
}

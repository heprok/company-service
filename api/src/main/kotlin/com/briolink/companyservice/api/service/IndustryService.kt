package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.domain.v1_0.IndustrySyncData
import com.briolink.companyservice.common.event.v1_0.IndustryCreatedEvent
import com.briolink.companyservice.common.event.v1_0.IndustrySyncEvent
import com.briolink.companyservice.common.jpa.write.entity.IndustryWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.IndustryWriteRepository
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
class IndustryService(
    private val industryWriteRepository: IndustryWriteRepository,
    private val eventPublisher: EventPublisher,
) {
    fun create(name: String): IndustryWriteEntity =
        industryWriteRepository.findByName(name) ?: IndustryWriteEntity().apply {
            this.name = name
            industryWriteRepository.save(this).let {
                eventPublisher.publish(IndustryCreatedEvent(it.toDomain()))
            }
        }

    fun findById(id: UUID) = industryWriteRepository.findById(id)

    @Async
    fun publishSyncEvent(syncId: Int, period: PeriodDateTime? = null) {
        var pageRequest = PageRequest.of(0, 200)
        var page = if (period == null) industryWriteRepository.findAll(pageRequest)
        else industryWriteRepository.findByCreatedOrChangedBetween(period.startInstants, period.endInstant, pageRequest)
        var indexRow = 0
        while (!page.isEmpty) {
            pageRequest = pageRequest.next()
            page.content.forEach {
                indexRow += 1
                eventPublisher.publish(
                    IndustrySyncEvent(
                        IndustrySyncData(
                            service = ServiceEnum.Company,
                            indexObjectSync = indexRow.toLong(),
                            totalObjectSync = page.totalElements,
                            syncId = syncId,
                            objectSync = it.toDomain()
                        )
                    )
                )
            }
            page = if (period == null) industryWriteRepository.findAll(pageRequest)
            else industryWriteRepository.findByCreatedOrChangedBetween(
                period.startInstants,
                period.endInstant,
                pageRequest
            )
        }
    }
}

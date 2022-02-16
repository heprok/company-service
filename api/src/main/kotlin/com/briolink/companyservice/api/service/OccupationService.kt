package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.domain.v1_0.OccupationSyncData
import com.briolink.companyservice.common.event.v1_0.OccupationCreatedEvent
import com.briolink.companyservice.common.event.v1_0.OccupationSyncEvent
import com.briolink.companyservice.common.jpa.write.entity.OccupationWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.OccupationWriteRepository
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
class OccupationService(
    private val occupationWriteRepository: OccupationWriteRepository,
    private val eventPublisher: EventPublisher,
) {
    fun create(name: String): OccupationWriteEntity =
        occupationWriteRepository.findByName(name) ?: OccupationWriteEntity().apply {
            this.name = name
            occupationWriteRepository.save(this).let {
                eventPublisher.publishAsync(OccupationCreatedEvent(it.toDomain()))
            }
        }

    fun findById(id: UUID) = occupationWriteRepository.findById(id)

    @Async
    fun publishSyncEvent(syncId: Int, period: PeriodDateTime? = null) {
        var pageRequest = PageRequest.of(0, 200)
        var page = if (period == null) occupationWriteRepository.findAll(pageRequest)
        else occupationWriteRepository.findByCreatedOrChangedBetween(
            period.startInstants,
            period.endInstant,
            pageRequest
        )
        var indexRow = 0
        while (!page.isEmpty) {
            pageRequest = pageRequest.next()
            page.content.forEach {
                indexRow += 1
                eventPublisher.publish(
                    OccupationSyncEvent(
                        OccupationSyncData(
                            service = ServiceEnum.Company,
                            indexObjectSync = indexRow.toLong(),
                            totalObjectSync = page.totalElements,
                            syncId = syncId,
                            objectSync = it.toDomain()
                        )

                    )
                )
            }
            page = if (period == null) occupationWriteRepository.findAll(pageRequest)
            else occupationWriteRepository.findByCreatedOrChangedBetween(
                period.startInstants,
                period.endInstant,
                pageRequest
            )
        }
    }
}

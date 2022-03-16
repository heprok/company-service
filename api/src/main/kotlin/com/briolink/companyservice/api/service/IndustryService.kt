package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.event.v1_0.IndustryCreatedEvent
import com.briolink.companyservice.common.event.v1_0.IndustrySyncEvent
import com.briolink.companyservice.common.jpa.write.entity.IndustryWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.IndustryWriteRepository
import com.briolink.lib.event.publisher.EventPublisher
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncUtil
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.model.PeriodDateTime
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

    private fun publishIndustrySyncEvent(
        syncId: Int,
        objectIndex: Long,
        totalObjects: Long,
        entity: IndustryWriteEntity?
    ) {
        eventPublisher.publishAsync(
            IndustrySyncEvent(
                SyncData(
                    objectIndex = objectIndex,
                    totalObjects = totalObjects,
                    objectSync = entity?.toDomain(),
                    syncId = syncId,
                    service = ServiceEnum.Company,
                ),
            ),
        )
    }

    fun publishSyncEvent(syncId: Int, period: PeriodDateTime? = null) {
        SyncUtil.publishSyncEvent(period, industryWriteRepository) { indexElement, totalElements, entity ->
            publishIndustrySyncEvent(
                syncId, indexElement, totalElements,
                entity as IndustryWriteEntity?,
            )
        }
    }
}

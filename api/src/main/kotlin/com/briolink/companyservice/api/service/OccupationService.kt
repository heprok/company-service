package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.event.v1_0.OccupationCreatedEvent
import com.briolink.companyservice.common.event.v1_0.OccupationSyncEvent
import com.briolink.companyservice.common.jpa.write.entity.OccupationWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.OccupationWriteRepository
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

    private fun publishOccupationSyncEvent(
        syncId: Int,
        objectIndex: Long,
        totalObjects: Long,
        entity: OccupationWriteEntity?
    ) {
        eventPublisher.publishAsync(
            OccupationSyncEvent(
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
        SyncUtil.publishSyncEvent(period, occupationWriteRepository) { indexElement, totalElements, entity ->
            publishOccupationSyncEvent(
                syncId, indexElement, totalElements,
                entity as OccupationWriteEntity?,
            )
        }
    }
}

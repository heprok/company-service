package com.briolink.companyservice.api.event

import com.briolink.companyservice.common.jpa.write.entity.EventStoreWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.EventStoreWriteRepository
import com.briolink.lib.event.publisher.EventStoreWriter
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class AppEventStoreWriter(
    private val eventStoreWriteRepository: EventStoreWriteRepository,
) : EventStoreWriter {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun write(payload: String, timestamp: Long) {
        // TODO: check for select
        eventStoreWriteRepository.save(EventStoreWriteEntity(payload, Instant.ofEpochMilli(timestamp)))
    }
}

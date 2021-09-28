package com.briolink.companyservice.api.event

import com.briolink.companyservice.common.jpa.write.entity.EventStoreWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.EventStoreWriteRepository
import com.briolink.event.AbstractEventPublisherHandler
import org.springframework.stereotype.Component
import com.amazonaws.services.sns.AmazonSNS
import java.time.Instant

@Component
class EventPublisherHandler(
    amazonSns: AmazonSNS,
    private val eventStoreWriteRepository: EventStoreWriteRepository
) :
    AbstractEventPublisherHandler(amazonSns) {
    override fun writeToEventStore(payload: String, timestamp: Long) {
        eventStoreWriteRepository.saveAndFlush(EventStoreWriteEntity(payload, Instant.ofEpochMilli(timestamp)))
    }
}

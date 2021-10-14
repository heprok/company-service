package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.event.v1_0.IndustryCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.IndustryReadRepository

@EventHandler("IndustryCreatedEvent", "1.0")
class IndustryCreatedEventHandler(
    private val industryReadRepository: IndustryReadRepository
) : IEventHandler<IndustryCreatedEvent> {
    override fun handle(event: IndustryCreatedEvent) {
        val eventData = event.data
        val industry = IndustryReadEntity().apply {
            this.id = eventData.id
            this.name = eventData.name

        }
        industryReadRepository.save(industry)
    }
}
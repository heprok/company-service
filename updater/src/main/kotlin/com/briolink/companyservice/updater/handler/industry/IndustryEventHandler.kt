package com.briolink.companyservice.updater.handler.industry

import com.briolink.companyservice.common.event.v1_0.IndustryCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.IndustryReadRepository
import org.springframework.transaction.annotation.Transactional

@EventHandler("IndustryCreatedEvent", "1.0")
@Transactional
class IndustryEventHandler(
    private val industryHandlerService: IndustryHandlerService
) : IEventHandler<IndustryCreatedEvent> {
    override fun handle(event: IndustryCreatedEvent) {
        industryHandlerService.create(event.data)
    }
}
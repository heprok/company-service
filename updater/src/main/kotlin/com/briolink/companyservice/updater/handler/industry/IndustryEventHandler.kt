package com.briolink.companyservice.updater.handler.industry

import com.briolink.companyservice.common.event.v1_0.IndustryCreatedEvent
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler

@EventHandler("IndustryCreatedEvent", "1.0")
class IndustryEventHandler(
    private val industryHandlerService: IndustryHandlerService
) : IEventHandler<IndustryCreatedEvent> {
    override fun handle(event: IndustryCreatedEvent) {
        industryHandlerService.create(event.data)
    }
}

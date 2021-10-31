package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.updater.event.CompanyServiceCreatedEvent
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.updater.handler.service.CompanyServiceHandlerService

@EventHandlers(
    EventHandler("CompanyServiceCreatedEvent", "1.0"),
    EventHandler("CompanyServiceUpdatedEvent", "1.0")
)
class CompanyServiceCreatedEventHandler(
    private val companyServiceReadRepository: ServiceReadRepository,
    private val companyServiceHandlerService: CompanyServiceHandlerService
) : IEventHandler<CompanyServiceCreatedEvent> {
    override fun handle(event: CompanyServiceCreatedEvent) {
        val companyService = event.data
        companyServiceHandlerService.createOrUpdate(companyService)
    }
}

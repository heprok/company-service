package com.briolink.companyservice.updater.handler.companyservice

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import org.springframework.transaction.annotation.Transactional

@EventHandlers(
    EventHandler("CompanyServiceCreatedEvent", "1.0"),
    EventHandler("CompanyServiceUpdatedEvent", "1.0")
)
@Transactional
class CompanyServiceCreatedEventHandler(
    private val companyServiceReadRepository: ServiceReadRepository,
    private val companyServiceHandlerService: CompanyServiceHandlerService
) : IEventHandler<CompanyServiceCreatedEvent> {
    override fun handle(event: CompanyServiceCreatedEvent) {
        val companyService = event.data
        companyServiceHandlerService.createOrUpdate(companyService)
    }
}

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
class CompanyServiceCreatedEventHandler(
    private val companyServiceReadRepository: ServiceReadRepository,
    private val companyServiceHandlerService: CompanyServiceHandlerService
) : IEventHandler<CompanyServiceCreatedEvent> {
    override fun handle(event: CompanyServiceCreatedEvent) {
        companyServiceHandlerService.createOrUpdate(event.data)
    }
}

@EventHandler("CompanyServiceDeletedEvent", "1.0")
class ServiceDeletedEventHandler(
    private val companyServiceHandlerService: CompanyServiceHandlerService
) : IEventHandler<CompanyServiceDeletedEvent> {
    override fun handle(event: CompanyServiceDeletedEvent) {
        companyServiceHandlerService.deleteById(event.data.id)
    }
}

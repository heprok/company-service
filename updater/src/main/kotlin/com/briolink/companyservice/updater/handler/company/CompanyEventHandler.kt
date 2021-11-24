package com.briolink.companyservice.updater.handler.company

import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers

@EventHandlers(
    EventHandler("CompanyCreatedEvent", "1.0"),
    EventHandler("CompanyUpdatedEvent", "1.0"),
)
class CompanyEventHandler(
    private val companyReadRepository: CompanyReadRepository,
    private val companyHandlerService: CompanyHandlerService,
) : IEventHandler<CompanyCreatedEvent> {
    override fun handle(event: CompanyCreatedEvent) {
        companyHandlerService.createOrUpdate(event.data).let {
            // TODO update connection
            if (event.name == "CompanyUpdatedEvent") {
            }
        }
    }
}

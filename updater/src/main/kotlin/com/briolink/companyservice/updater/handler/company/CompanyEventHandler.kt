package com.briolink.companyservice.updater.handler.company

import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import org.springframework.transaction.annotation.Transactional

@EventHandlers(
    EventHandler("CompanyCreatedEvent", "1.0"),
    EventHandler("CompanyUpdatedEvent", "1.0")
)
@Transactional
class CompanyEventHandler(
    private val companyReadRepository: CompanyReadRepository,
    private val companyHandlerService: CompanyHandlerService,
) : IEventHandler<CompanyCreatedEvent> {
    override fun handle(event: CompanyCreatedEvent) {
        val company = event.data
        companyHandlerService.createOrUpdate(company)
    }
}

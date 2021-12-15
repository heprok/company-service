package com.briolink.companyservice.updater.handler.companyservice

import com.briolink.companyservice.common.event.v1_0.RefreshConnectionServiceEvent
import com.briolink.companyservice.common.jpa.runAfterTxCommit
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import org.springframework.context.ApplicationEventPublisher

@EventHandlers(
    EventHandler("CompanyServiceCreatedEvent", "1.0"),
    EventHandler("CompanyServiceUpdatedEvent", "1.0"),
)
class CompanyServiceCreatedEventHandler(
    private val companyServiceHandlerService: CompanyServiceHandlerService
) : IEventHandler<CompanyServiceCreatedEvent> {
    override fun handle(event: CompanyServiceCreatedEvent) {
        companyServiceHandlerService.createOrUpdate(event.data)
    }
}

@EventHandler("RefreshConnectionServiceEvent", "1.0")
class RefreshConnectionServiceEventHandler(
    private val companyServiceHandlerService: CompanyServiceHandlerService
) : IEventHandler<RefreshConnectionServiceEvent> {
    override fun handle(event: RefreshConnectionServiceEvent) {
        runAfterTxCommit { companyServiceHandlerService.refreshVerifyUses(event.data.serviceId) }
    }
}

@EventHandler("CompanyServiceDeletedEvent", "1.0")
class ServiceDeletedEventHandler(
    private val companyServiceHandlerService: CompanyServiceHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<CompanyServiceDeletedEvent> {
    override fun handle(event: CompanyServiceDeletedEvent) {
        companyServiceHandlerService.deleteById(event.data.id)
        runAfterTxCommit { applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(event.data.companyId, false)) }
    }
}

@EventHandler("CompanyServiceHideEvent", "1.0")
class ServiceHideEventHandler(
    private val companyServiceHandlerService: CompanyServiceHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<CompanyServiceHideEvent> {
    override fun handle(event: CompanyServiceHideEvent) {
        companyServiceHandlerService.setHidden(event.data.id, event.data.hidden)
        runAfterTxCommit { applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(event.data.companyId, false)) }
    }
}

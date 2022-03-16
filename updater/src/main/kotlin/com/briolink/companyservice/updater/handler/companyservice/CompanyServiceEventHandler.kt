package com.briolink.companyservice.updater.handler.companyservice

import com.briolink.companyservice.common.event.v1_0.RefreshConnectionServiceEvent
import com.briolink.companyservice.common.jpa.runAfterTxCommit
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
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
        runAfterTxCommit {
            applicationEventPublisher.publishEvent(
                RefreshStatisticByCompanyId(
                    event.data.companyId,
                    false
                )
            )
        }
    }
}

@EventHandler("CompanyServiceHideEvent", "1.0")
class ServiceHideEventHandler(
    private val companyServiceHandlerService: CompanyServiceHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<CompanyServiceHideEvent> {
    override fun handle(event: CompanyServiceHideEvent) {
        companyServiceHandlerService.setHidden(event.data.id, event.data.hidden)
        runAfterTxCommit {
            applicationEventPublisher.publishEvent(
                RefreshStatisticByCompanyId(
                    event.data.companyId,
                    false
                )
            )
        }
    }
}

@EventHandler("CompanyServiceSyncEvent", "1.0")
class CompanyServiceSyncEventHandler(
    private val companyServiceHandlerService: CompanyServiceHandlerService,
    syncService: SyncService,
) : SyncEventHandler<CompanyServiceSyncEvent>(ObjectSyncEnum.CompanyService, syncService) {
    override fun handle(event: CompanyServiceSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            companyServiceHandlerService.createOrUpdate(objectSync)
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

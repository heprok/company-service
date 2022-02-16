package com.briolink.companyservice.updater.handler.companyservice

import com.briolink.companyservice.common.event.v1_0.RefreshConnectionServiceEvent
import com.briolink.companyservice.common.jpa.enumeration.ObjectSyncEnum
import com.briolink.companyservice.common.jpa.runAfterTxCommit
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError
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
    private val syncService: SyncService,
) : IEventHandler<CompanyServiceSyncEvent> {
    override fun handle(event: CompanyServiceSyncEvent) {
        val syncData = event.data
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSync(syncData.syncId, syncData.service)
        try {
            companyServiceHandlerService.createOrUpdate(syncData.objectSync)
        } catch (ex: Exception) {
            syncService.sendSyncError(
                syncError = SyncError(
                    service = syncData.service,
                    updater = UpdaterEnum.Company,
                    syncId = syncData.syncId,
                    exception = ex,
                    indexObjectSync = syncData.indexObjectSync
                )
            )
        }
        if (syncData.indexObjectSync == syncData.totalObjectSync)
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.CompanyService)
    }
}

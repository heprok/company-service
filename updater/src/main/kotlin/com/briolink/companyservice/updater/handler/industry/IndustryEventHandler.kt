package com.briolink.companyservice.updater.handler.industry

import com.briolink.companyservice.common.event.v1_0.IndustryCreatedEvent
import com.briolink.companyservice.common.event.v1_0.IndustrySyncEvent
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError

@EventHandler("IndustryCreatedEvent", "1.0")
class IndustryEventHandler(
    private val industryHandlerService: IndustryHandlerService
) : IEventHandler<IndustryCreatedEvent> {
    override fun handle(event: IndustryCreatedEvent) {
        industryHandlerService.createOrUpdate(null, event.data)
    }
}

@EventHandler("IndustrySyncEvent", "1.0")
class IndustrySyncEventHandler(
    private val industryHandlerService: IndustryHandlerService,
    private val syncService: SyncService,
) : IEventHandler<IndustrySyncEvent> {
    override fun handle(event: IndustrySyncEvent) {
        val syncData = event.data
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSyncForService(syncData.syncId, syncData.service)
        try {
            val objectSync = syncData.objectSync
            val industry = industryHandlerService.findById(objectSync.id)
            industryHandlerService.createOrUpdate(industry, objectSync)
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
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.CompanyIndustry)
    }
}

package com.briolink.companyservice.updater.handler.industry

import com.briolink.companyservice.common.event.v1_0.IndustryCreatedEvent
import com.briolink.companyservice.common.event.v1_0.IndustrySyncEvent
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum

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
    syncService: SyncService,
) : SyncEventHandler<IndustrySyncEvent>(ObjectSyncEnum.CompanyIndustry, syncService) {
    override fun handle(event: IndustrySyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            val industry = industryHandlerService.findById(objectSync.id)
            industryHandlerService.createOrUpdate(industry, objectSync)
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

package com.briolink.companyservice.updater.handler.occupation

import com.briolink.companyservice.common.event.v1_0.OccupationCreatedEvent
import com.briolink.companyservice.common.event.v1_0.OccupationSyncEvent
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum

@EventHandler("OccupationCreatedEvent", "1.0")
class OccupationEventHandler(
    private val occupationHandlerService: OccupationHandlerService
) : IEventHandler<OccupationCreatedEvent> {
    override fun handle(event: OccupationCreatedEvent) {
        occupationHandlerService.createOrUpdate(null, event.data)
    }
}

@EventHandler("OccupationSyncEvent", "1.0")
class OccupationSyncEventHandler(
    private val occupationHandlerService: OccupationHandlerService,
    syncService: SyncService,
) : SyncEventHandler<OccupationSyncEvent>(ObjectSyncEnum.CompanyOccupation, syncService) {
    override fun handle(event: OccupationSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            val occupation = occupationHandlerService.findById(objectSync.id)
            occupationHandlerService.createOrUpdate(occupation, objectSync)
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

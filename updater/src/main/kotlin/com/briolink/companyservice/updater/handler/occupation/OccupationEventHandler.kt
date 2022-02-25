package com.briolink.companyservice.updater.handler.occupation

import com.briolink.companyservice.common.event.v1_0.OccupationCreatedEvent
import com.briolink.companyservice.common.event.v1_0.OccupationSyncEvent
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError

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
    private val syncService: SyncService,
) : IEventHandler<OccupationSyncEvent> {
    override fun handle(event: OccupationSyncEvent) {
        val syncData = event.data
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSyncForService(syncData.syncId, syncData.service)
        try {
            val objectSync = syncData.objectSync
            val occupation = occupationHandlerService.findById(objectSync.id)
            occupationHandlerService.createOrUpdate(occupation, objectSync)
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
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.CompanyOccupation)
    }
}

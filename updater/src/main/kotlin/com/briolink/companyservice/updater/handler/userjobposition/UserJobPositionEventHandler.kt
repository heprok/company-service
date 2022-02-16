package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.companyservice.common.jpa.enumeration.ObjectSyncEnum
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError

@EventHandlers(
    EventHandler("UserJobPositionCreatedEvent", "1.0"),
    EventHandler("UserJobPositionUpdatedEvent", "1.0")
)
class UserJobPositionCreatedEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
) : IEventHandler<UserJobPositionUpdatedEvent> {
    override fun handle(event: UserJobPositionUpdatedEvent) {
        userJobPositionHandlerService.createOrUpdate(event.data)
    }
}

@EventHandler("UserJobPositionDeletedEvent", "1.0")
class UserJobPositionDeletedEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
) : IEventHandler<UserJobPositionDeletedEvent> {
    override fun handle(event: UserJobPositionDeletedEvent) {
        userJobPositionHandlerService.delete(event.data.id)
    }
}

@EventHandler("UserJobPositionSyncEvent", "1.0")
class UserJobPositionSyncEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
    private val syncService: SyncService,
) : IEventHandler<UserJobPositionSyncEvent> {
    override fun handle(event: UserJobPositionSyncEvent) {
        val syncData = event.data
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSync(syncData.syncId, syncData.service)
        try {
            userJobPositionHandlerService.createOrUpdate(syncData.objectSync)
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
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.UserJobPosition)
    }
}

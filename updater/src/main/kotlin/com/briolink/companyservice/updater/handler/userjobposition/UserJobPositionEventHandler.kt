package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.companyservice.updater.service.SyncService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum

@EventHandlers(
    EventHandler("UserJobPositionCreatedEvent", "1.0"),
    EventHandler("UserJobPositionUpdatedEvent", "1.0")
)
class UserJobPositionCreatedEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
) : IEventHandler<UserJobPositionUpdatedEvent> {
    override fun handle(event: UserJobPositionUpdatedEvent) {
        if (event.data.startDate != null) userJobPositionHandlerService.createOrUpdate(event.data)
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
    syncService: SyncService,
) : SyncEventHandler<UserJobPositionSyncEvent>(ObjectSyncEnum.UserJobPosition, syncService) {
    override fun handle(event: UserJobPositionSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return

        try {
            val objectSync = syncData.objectSync!!
            if (syncData.objectSync?.startDate != null) userJobPositionHandlerService.createOrUpdate(objectSync)
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

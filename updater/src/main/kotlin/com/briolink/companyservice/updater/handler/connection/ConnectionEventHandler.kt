package com.briolink.companyservice.updater.handler.connection

import com.briolink.companyservice.updater.service.SyncService
import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import javax.transaction.Transactional

@EventHandlers(
    EventHandler("ConnectionCreatedEvent", "1.0"),
    EventHandler("ConnectionUpdatedEvent", "1.0"),
    EventHandler("ConnectionAcceptedEvent", "1.0"),
    EventHandler("ConnectionVisibilityUpdatedEvent", "1.0"),
    EventHandler("ConnectionDeletedEvent", "1.0"),
)
@Transactional
class ConnectionEventHandler(
    private val connectionHandlerService: ConnectionHandlerService
) : IEventHandler<ConnectionCreatedEvent> {
    override fun handle(event: ConnectionCreatedEvent) {
        connectionHandlerService.createOrUpdate(event.data)
    }
}

@EventHandler("ConnectionSyncEvent", "1.0")
class ConnectionSyncEventHandler(
    private val connectionHandlerService: ConnectionHandlerService,
    syncService: SyncService,
) : SyncEventHandler<ConnectionSyncEvent>(ObjectSyncEnum.Connection, syncService) {
    override fun handle(event: ConnectionSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            connectionHandlerService.createOrUpdate(objectSync)
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }

        if (objectSyncCompleted(syncData))
            connectionHandlerService.refreshAllConnectionStatistic()
    }
}

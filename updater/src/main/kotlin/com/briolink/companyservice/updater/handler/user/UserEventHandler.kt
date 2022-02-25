package com.briolink.companyservice.updater.handler.user

import com.briolink.companyservice.updater.handler.connection.ConnectionHandlerService
import com.briolink.companyservice.updater.handler.userjobposition.UserJobPositionHandlerService
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError

@EventHandlers(
    EventHandler("UserCreatedEvent", "1.0"),
    EventHandler("UserUpdatedEvent", "1.0"),
)
class UserEventHandler(
    private val userHandlerService: UserHandlerService,
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
    private val connectionHandlerService: ConnectionHandlerService
) : IEventHandler<UserCreatedEvent> {
    override fun handle(event: UserCreatedEvent) {
        userHandlerService.createOrUpdate(event.data).also {
            if (event.name == "UserUpdatedEvent") {
                userJobPositionHandlerService.updateUser(it)
                connectionHandlerService.updateUser(it)
            }
        }
    }
}

@EventHandler("UserSyncEvent", "1.0")
class UserSyncEventHandler(
    private val userHandlerService: UserHandlerService,
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
    private val connectionHandlerService: ConnectionHandlerService,
    private val syncService: SyncService,
) : IEventHandler<UserSyncEvent> {
    override fun handle(event: UserSyncEvent) {
        val syncData = event.data
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSyncForService(syncData.syncId, syncData.service)
        try {
            userHandlerService.createOrUpdate(syncData.objectSync).also {
                userJobPositionHandlerService.updateUser(it)
                connectionHandlerService.updateUser(it)
            }
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
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.User)
    }
}

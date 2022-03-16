package com.briolink.companyservice.updater.handler.user

import com.briolink.companyservice.updater.handler.connection.ConnectionHandlerService
import com.briolink.companyservice.updater.handler.userjobposition.UserJobPositionHandlerService
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum

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
    syncService: SyncService,
) : SyncEventHandler<UserSyncEvent>(ObjectSyncEnum.User, syncService) {
    override fun handle(event: UserSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            userHandlerService.createOrUpdate(objectSync).also {
                userJobPositionHandlerService.updateUser(it)
                connectionHandlerService.updateUser(it)
            }
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

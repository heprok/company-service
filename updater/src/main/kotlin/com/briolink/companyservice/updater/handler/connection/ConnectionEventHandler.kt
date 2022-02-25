package com.briolink.companyservice.updater.handler.connection

import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError
import org.springframework.context.ApplicationEventPublisher

@EventHandlers(
    EventHandler("ConnectionCreatedEvent", "1.0"),
    EventHandler("ConnectionUpdatedEvent", "1.0"),
)
class ConnectionEventHandler(
    private val connectionHandlerService: ConnectionHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<ConnectionCreatedEvent> {
    override fun handle(event: ConnectionCreatedEvent) {
        val connection = event.data
        if (connection.status != ConnectionStatus.Rejected) {
            connectionHandlerService.createOrUpdate(connection).also {
                if (connection.status == ConnectionStatus.Verified) {
                    applicationEventPublisher.publishEvent(
                        RefreshStatisticByCompanyId(
                            it.participantToCompanyId,
                            false
                        )
                    )
                    applicationEventPublisher.publishEvent(
                        RefreshStatisticByCompanyId(
                            it.participantFromCompanyId,
                            false
                        )
                    )
                }
            }
        } else if (connection.status == ConnectionStatus.Rejected) {
            connectionHandlerService.delete(connection.id)
        }
    }
}

@EventHandler("ConnectionSyncEvent", "1.0")
class ConnectionSyncEventHandler(
    private val connectionHandlerService: ConnectionHandlerService,
    private val syncService: SyncService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<ConnectionSyncEvent> {
    override fun handle(event: ConnectionSyncEvent) {
        val syncData = event.data
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSyncForService(syncData.syncId, syncData.service)
        if (syncData.objectSync == null) {
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.Connection)
            return
        }
        try {
            val connection = syncData.objectSync
            if (connection.status != ConnectionStatus.Rejected) {
                connectionHandlerService.createOrUpdate(connection).also {
                    if (connection.status == ConnectionStatus.Verified) {
                        applicationEventPublisher.publishEvent(
                            RefreshStatisticByCompanyId(
                                it.participantToCompanyId,
                                false
                            )
                        )
                        applicationEventPublisher.publishEvent(
                            RefreshStatisticByCompanyId(
                                it.participantFromCompanyId,
                                false
                            )
                        )
                    }
                }
            } else if (connection.status == ConnectionStatus.Rejected) {
                connectionHandlerService.delete(connection.id)
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
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.Connection)
    }
}

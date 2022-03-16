package com.briolink.companyservice.updater.handler.connection

import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
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
    private val applicationEventPublisher: ApplicationEventPublisher,
    syncService: SyncService,
) : SyncEventHandler<ConnectionSyncEvent>(ObjectSyncEnum.Connection, syncService) {
    override fun handle(event: ConnectionSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val connection = syncData.objectSync!!
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
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

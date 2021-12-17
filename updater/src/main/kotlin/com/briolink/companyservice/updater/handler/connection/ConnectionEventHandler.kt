package com.briolink.companyservice.updater.handler.connection

import com.briolink.companyservice.common.jpa.runAfterTxCommit
import com.briolink.companyservice.updater.handler.statistic.StatisticHandlerService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers

@EventHandlers(
    EventHandler("ConnectionCreatedEvent", "1.0"),
    EventHandler("ConnectionUpdatedEvent", "1.0"),
)
class ConnectionEventHandler(
    private val connectionHandlerService: ConnectionHandlerService,
    private val statisticHandlerService: StatisticHandlerService,
) : IEventHandler<ConnectionCreatedEvent> {
    override fun handle(event: ConnectionCreatedEvent) {
        val connection = event.data
        if (connection.status != ConnectionStatus.Rejected) {
            connectionHandlerService.createOrUpdate(connection).also {
                if (connection.status == ConnectionStatus.Verified) {
                    runAfterTxCommit { statisticHandlerService.refreshByCompanyId(it.participantToCompanyId) }
                    runAfterTxCommit { statisticHandlerService.refreshByCompanyId(it.participantFromCompanyId) }
                }
            }
        } else if (connection.status == ConnectionStatus.Rejected) {
            connectionHandlerService.delete(connection.id)
        }
    }
}

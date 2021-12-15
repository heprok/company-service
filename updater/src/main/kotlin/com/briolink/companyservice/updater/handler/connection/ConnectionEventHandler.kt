package com.briolink.companyservice.updater.handler.connection

import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
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
            connectionHandlerService.createOrUpdate(connection).let {
                if (connection.status == ConnectionStatus.Verified) {
                    applicationEventPublisher.publishEvent(
                        RefreshStatisticByCompanyId(
                            connection.participantTo.companyId,
                            false
                        )
                    )
                    applicationEventPublisher.publishEvent(
                        RefreshStatisticByCompanyId(
                            connection.participantFrom.companyId,
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

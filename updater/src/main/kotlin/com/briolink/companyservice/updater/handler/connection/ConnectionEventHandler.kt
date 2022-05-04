package com.briolink.companyservice.updater.handler.connection

import com.briolink.companyservice.common.jpa.enumeration.ConnectionObjectTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.updater.RefreshStatisticConnectionByCompanyId
import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import org.springframework.context.ApplicationEventPublisher
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
    private val connectionReadRepository: ConnectionReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher

) : IEventHandler<ConnectionCreatedEvent> {
    override fun handle(event: ConnectionCreatedEvent) {
        val connection = event.data
        var updateStatistic = false

        if (connection.status == ConnectionStatus.Connected &&
            connection.fromObjectType == ConnectionObjectType.Company || connection.toObjectType == ConnectionObjectType.Company
        ) {
            connectionReadRepository.findByFromObjectIdAndToObjectId(connection.fromObjectId, connection.toObjectId!!)
                .orElse(ConnectionReadEntity())
                .apply {
                    updateStatistic = if (this.id == null) true
                    else this.hidden != connection.hidden

                    this.toObjectId = connection.toObjectId
                    this.fromObjectId = connection.fromObjectId
                    this.fromObjectType = ConnectionObjectTypeEnum.ofValue(connection.fromObjectType.value)
                    this.toObjectType = ConnectionObjectTypeEnum.ofValue(connection.toObjectType.value)
                    this.hidden = connection.hidden

                    connectionReadRepository.save(this)
                }
        } else connection.toObjectId?.let {
            updateStatistic = connectionReadRepository.deleteBy(connection.fromObjectId, it) > 0
        }

        if (updateStatistic) {
            if (connection.fromObjectType == ConnectionObjectType.Company)
                applicationEventPublisher.publishEvent(RefreshStatisticConnectionByCompanyId(connection.fromObjectId))

            if (connection.toObjectType == ConnectionObjectType.Company)
                connection.toObjectId?.also { applicationEventPublisher.publishEvent(RefreshStatisticConnectionByCompanyId(it)) }
        }
    }
}

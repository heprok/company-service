package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.updater.dto.Connection
import com.briolink.companyservice.updater.dto.ConnectionStatus
import com.briolink.companyservice.updater.dto.RoleType
import com.briolink.companyservice.updater.event.ConnectionCreatedEvent
import com.briolink.companyservice.updater.event.ConnectionUpdatedEvent
import com.briolink.companyservice.updater.service.CompanyService
import com.briolink.companyservice.updater.service.ConnectionService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler

@EventHandler("UserJobPositionCreatedEvent", "1.0")
class ConnectionCreatedEventHandler(
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyService: CompanyService,
    private val connectionService: ConnectionService,
) : IEventHandler<ConnectionCreatedEvent> {
    override fun handle(event: ConnectionCreatedEvent) {
        val connection = event.data

        if (connection.participantFrom.companyRole.type == RoleType.Buyer) {
            connection.participantTo = connection.participantFrom.also {
                connection.participantFrom = connection.participantTo
            }
            if (companyService.getPermission(
                        userId = connection.participantFrom.userId,
                        companyId = connection.participantFrom.companyId,
                ) != null &&
                companyService.getPermission(
                        userId = connection.participantTo.userId,
                        companyId = connection.participantTo.companyId,
                ) != null) {
                connectionService.create(connection)
            }
        }
    }

    @EventHandler("ConnectionUpdatedEvent", "1.0")
    class ConnectionUpdatedEventHandler(
        private val connectionReadRepository: ConnectionReadRepository,
        private val connectionService: ConnectionService,
        private val companyService: CompanyService,
    ) : IEventHandler<ConnectionUpdatedEvent> {
        override fun handle(event: ConnectionUpdatedEvent) {
            val connection: Connection = event.data
            if (connection.participantFrom.companyRole.type == RoleType.Buyer) {
                connection.participantTo = connection.participantFrom.also {
                    connection.participantFrom = connection.participantTo
                }
            }
            val buyerUserRole = companyService.getPermission(
                    userId = connection.participantTo.userId,
                    companyId = connection.participantTo.companyId,
            )
            val sellerUserRole = companyService.getPermission(
                    userId = connection.participantFrom.userId,
                    companyId = connection.participantFrom.companyId,
            )
            if (connection.status == ConnectionStatus.Verified) {
                if (buyerUserRole == null && sellerUserRole == null) {
                    if (
                        companyService.setOwner(companyId = connection.participantTo.companyId, userId = connection.participantTo.userId) &&
                        companyService.setOwner(
                                companyId = connection.participantFrom.companyId,
                                userId = connection.participantFrom.userId,
                        )) {
                        connectionService.create(connection)
                        connectionService.setStatus(ConnectionReadEntity.ConnectionStatus.Verified, connection.id)
                    }

                } else if (sellerUserRole != null && buyerUserRole != null) {
                    connectionService.setStatus(ConnectionReadEntity.ConnectionStatus.Verified, connection.id)
                }


            }
            if (connection.status == ConnectionStatus.InProgress) {

            }
            if (connection.status == ConnectionStatus.Pending) {

            }

        }
    }
}

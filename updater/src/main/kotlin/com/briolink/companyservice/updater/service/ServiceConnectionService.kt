package com.briolink.companyservice.updater.service

import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.connection.service.ConnectionServiceReadRepository
import com.briolink.companyservice.updater.dto.ConnectionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@Service
class ServiceConnectionService(
    private val connectionServiceReadRepository: ConnectionServiceReadRepository
) {
    fun addConnectionService(buyerCompanyId: UUID, sellerCompanyId: UUID, connectionId: UUID, connectionService: ConnectionService) {
        if (connectionService.serviceId != null) {
            if (!connectionServiceReadRepository.existsByConnectionIdAndCompanyIdAndServiceId(
                        companyId = buyerCompanyId,
                        connectionId = connectionId,
                        serviceId = connectionService.id,
                )) {
                connectionServiceReadRepository.save(
                        ConnectionServiceReadEntity(
                                connectionId = connectionId,
                                companyId = buyerCompanyId,
                                serviceId = connectionService.id,
                                serviceName = connectionService.serviceName,
                        ),
                )
            }
            if (!connectionServiceReadRepository.existsByConnectionIdAndCompanyIdAndServiceId(
                        companyId = sellerCompanyId,
                        connectionId = connectionId,
                        serviceId = connectionService.id,
                )) {
                connectionServiceReadRepository.save(
                        ConnectionServiceReadEntity(
                                connectionId = connectionId,
                                companyId = sellerCompanyId,
                                serviceId = connectionService.id,
                                serviceName = connectionService.serviceName,
                        ),
                )
            }

        }
    }
}

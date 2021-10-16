package com.briolink.companyservice.updater.service

import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.IndustryReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.StatisticReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.dto.Connection
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class ConnectionService(
    private val statisticReadRepository: StatisticReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val industryReadRepository: IndustryReadRepository,
    private val userReadRepository: UserReadRepository
) {
    fun create(connection: Connection) {
        val industryRead = industryReadRepository.getById(connection.participantTo.companyId)
        val sellerRead = companyReadRepository.getById(connection.participantFrom.companyId)
        val buyerRead = companyReadRepository.getById(connection.participantTo.companyId)
        val userBuyerRead = userReadRepository.getById(connection.participantTo.userId)
        val userSellerRead = userReadRepository.getById(connection.participantFrom.userId)

        val connectionRead = ConnectionReadEntity(connection.id).apply {

            sellerId = connection.participantFrom.companyId
            buyerId = connection.participantTo.companyId
            buyerRoleId = connection.participantTo.companyRole.id
            sellerRoleId = connection.participantFrom.companyRole.id
            industryId = industryRead.id
            verificationStage = ConnectionReadEntity.ConnectionStatus.Pending
            created = LocalDate.now()
            data = ConnectionReadEntity.Data(connection.id).apply {
                val endDateMutableList = mutableListOf<String>()
                val startDateMutableList = mutableListOf<String>()
                val idMutableList = mutableListOf<String>()
                val servicesConnection = mutableListOf<ConnectionReadEntity.Service>()

                connection.services.forEach { connectionService ->
                    val serviceReadEntity = serviceReadRepository.findById(connectionService.serviceId)
                    val serviceConnection = if (serviceReadEntity.isEmpty) {
                        ConnectionReadEntity.Service(
                                id = connectionService.serviceId,
                                name = connectionService.serviceName,
                                endDate = connectionService.endDate,
                                startDate = connectionService.startDate,
                        )
                    } else {
                        serviceReadEntity.get().let{
                            ConnectionReadEntity.Service(
                                    id = it.id,
                                    name = it.data.name,
                                    slug = it.data.slug,
                                    endDate = connectionService.endDate,
                                    startDate = connectionService.startDate
                            )
                        }
                    }
                    servicesConnection.add(serviceConnection)
                    idMutableList.add(connectionService.serviceId.toString())
                    startDateMutableList.add(connectionService.startDate.toString())
                    endDateMutableList.add(connectionService.endDate.toString())
                }
                industry = ConnectionReadEntity.Industry(
                        id = industryRead.id,
                        name = industryRead.name,
                )
                buyerCompany = ConnectionReadEntity.ParticipantCompany(
                        id = buyerRead.id,
                        name = buyerRead.data.name,
                        slug = buyerRead.slug,
                        logo = URL(buyerRead.data.logo),
                        verifyUser = ConnectionReadEntity.VerifyUser(
                                id = userBuyerRead.id,
                                firstName = userBuyerRead.data.firstName,
                                lastName = userBuyerRead.data.lastName,
                                image = userBuyerRead.data.image,
                                slug = userBuyerRead.data.slug,
                        ),
                        role = ConnectionReadEntity.Role(
                                id = connection.participantTo.companyRole.id,
                                name = connection.participantTo.companyRole.name,
                        ),
                )

                sellerCompany = ConnectionReadEntity.ParticipantCompany(
                        id = sellerRead.id,
                        name = sellerRead.data.name,
                        slug = sellerRead.slug,
                        logo = URL(sellerRead.data.logo),
                        verifyUser = ConnectionReadEntity.VerifyUser(
                                id = userSellerRead.id,
                                firstName = userSellerRead.data.firstName,
                                lastName = userSellerRead.data.lastName,
                                image = userSellerRead.data.image,
                                slug = userSellerRead.data.slug,
                        ),
                        role = ConnectionReadEntity.Role(
                                id = connection.participantFrom.companyRole.id,
                                name = connection.participantFrom.companyRole.name,
                        ),
                )

                datesEndCollaboration = endDateMutableList.joinToString { ";" }
                datesStartCollaboration = startDateMutableList.joinToString { ";" }
                serviceIds = idMutableList.joinToString { ";" }
                services = servicesConnection

            }
        }
        connectionReadRepository.save(connectionRead)
    }

    fun setStatus(status: ConnectionReadEntity.ConnectionStatus, connectionId: UUID) {
        connectionReadRepository.save(
                connectionReadRepository.findById(connectionId)
                        .orElseThrow { throw EntityNotFoundException("$connectionId connection not found") }.apply {
                            verificationStage = status
                        },
        )
    }
}


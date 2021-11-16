package com.briolink.companyservice.updater.handler.connection

import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Year
import java.util.*
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class ConnectionHandlerService(
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val userReadRepository: UserReadRepository,
    private val serviceConnectionService: ServiceConnectionHandlerService
) {
    fun createOrUpdate(connection: Connection) {
        val sellerRead = companyReadRepository.getById(connection.participantFrom.companyId!!)
        val buyerRead = companyReadRepository.getById(connection.participantTo.companyId!!)
        val userBuyerRead = userReadRepository.getById(connection.participantTo.userId!!)
        val userSellerRead = userReadRepository.getById(connection.participantFrom.userId!!)
        connectionReadRepository.findById(connection.id).orElse(ConnectionReadEntity(connection.id)).apply {

            sellerId = connection.participantFrom.companyId!!
            buyerId = connection.participantTo.companyId!!
            buyerName = buyerRead.data.name
            sellerName = sellerRead.data.name
            location = buyerRead.data.location
            buyerRoleId = connection.participantTo.companyRole!!.id
            sellerRoleId = connection.participantFrom.companyRole!!.id
            industryId = buyerRead.data.industry!!.id
            verificationStage = ConnectionReadEntity.ConnectionStatus.valueOf(connection.status.name)
            created = connection.created
            data = ConnectionReadEntity.Data(connection.id).apply {
                val endDateMutableList = mutableListOf<Year?>()
                val startDateMutableList = mutableListOf<Year>()
                val idServiceMutableList = mutableListOf<UUID>()
                val servicesConnection = mutableListOf<ConnectionReadEntity.Service>()

                industry = buyerRead.data.industry?.let {
                    ConnectionReadEntity.Industry(
                            id = it.id,
                            name = it.name,
                    )
                }
                buyerCompany = ConnectionReadEntity.ParticipantCompany(
                        id = buyerRead.id,
                        name = buyerRead.data.name,
                        slug = buyerRead.slug,
                        logo = buyerRead.data.logo,
                        verifyUser = ConnectionReadEntity.VerifyUser(
                                id = userBuyerRead.id,
                                firstName = userBuyerRead.data.firstName,
                                lastName = userBuyerRead.data.lastName,
                                image = userBuyerRead.data.image,
                                slug = userBuyerRead.data.slug,
                        ),
                        role = ConnectionReadEntity.Role(
                                id = connection.participantTo.companyRole!!.id,
                                name = connection.participantTo.companyRole!!.name,
                                type = ConnectionRoleReadEntity.RoleType.values()[connection.participantTo.companyRole!!.type.ordinal],
                        ),
                )

                sellerCompany = ConnectionReadEntity.ParticipantCompany(
                        id = sellerRead.id,
                        name = sellerRead.data.name,
                        slug = sellerRead.slug,
                        logo = sellerRead.data.logo,
                        verifyUser = ConnectionReadEntity.VerifyUser(
                                id = userSellerRead.id,
                                firstName = userSellerRead.data.firstName,
                                lastName = userSellerRead.data.lastName,
                                image = userSellerRead.data.image,
                                slug = userSellerRead.data.slug,
                        ),
                        role = ConnectionReadEntity.Role(
                                id = connection.participantFrom.companyRole!!.id,
                                name = connection.participantFrom.companyRole!!.name,
                                type = ConnectionRoleReadEntity.RoleType.values()[connection.participantFrom.companyRole!!.type.ordinal],
                        ),
                )

                connection.services.forEach { connectionService ->
                    val serviceReadEntity = connectionService.serviceId?.let { serviceReadRepository.findById(it) }
                    val connectionServiceRead = serviceReadEntity?.get()?.let {

                        ConnectionReadEntity.Service(
                                id = it.id,
                                name = it.name,
                                slug = it.data.slug,
                                endDate = connectionService.endDate,
                                startDate = connectionService.startDate!!,
                        )
                    }
                        ?: ConnectionReadEntity.Service(
                                id = connectionService.serviceId,
                                name = connectionService.serviceName,
                                endDate = connectionService.endDate,
                                startDate = connectionService.startDate!!,
                        )
                    servicesConnection.add(connectionServiceRead)
                    serviceConnectionService.addConnectionService(
                            buyerCompanyId = buyerCompany.id,
                            sellerCompanyId = sellerCompany.id,
                            connectionService = connectionService,
                            connectionId = connection.id,
                    )
                    idServiceMutableList.add(connectionService.serviceId!!)
                    startDateMutableList.add(connectionService.startDate!!)
                    endDateMutableList.add(connectionService.endDate)
                }

                startCollaboration = startDateMutableList.minOrNull()!!
                endCollaboration = if (endDateMutableList.contains(null)) null else endDateMutableList.maxByOrNull { year -> year!! }
                serviceIds = idServiceMutableList
                services = servicesConnection
            }
            connectionReadRepository.save(this)
        }
    }

    fun setStatus(status: ConnectionReadEntity.ConnectionStatus, connectionId: UUID) {
        connectionReadRepository.save(
                connectionReadRepository.findById(connectionId)
                        .orElseThrow { throw EntityNotFoundException("$connectionId connection not found") }.apply {
                            verificationStage = status
                        },
        )
    }

    fun delete(connectionId: UUID) {
        connectionReadRepository.deleteById(connectionId)
    }

}


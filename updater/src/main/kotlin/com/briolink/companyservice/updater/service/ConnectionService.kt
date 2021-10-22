package com.briolink.companyservice.updater.service

import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.IndustryReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.StatisticReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.dto.Connection
import com.briolink.companyservice.updater.dto.ConnectionRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityNotFoundException
import kotlin.random.Random

@Transactional
@Service
class ConnectionService(
    private val statisticReadRepository: StatisticReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val industryReadRepository: IndustryReadRepository,
    private val userReadRepository: UserReadRepository,
) {
    fun create(connection: Connection) {
        val sellerRead = companyReadRepository.getById(connection.participantFrom.companyId)
        val buyerRead = companyReadRepository.getById(connection.participantTo.companyId)
        val userBuyerRead = userReadRepository.getById(connection.participantTo.userId)
        val userSellerRead = userReadRepository.getById(connection.participantFrom.userId)
        val industryRead = IndustryReadEntity(
                id = UUID.fromString(buyerRead.data.industry!!.id),
        ).apply {
            name = buyerRead.data.industry!!.name
        }
        val connectionRead = ConnectionReadEntity(connection.id).apply {

            sellerId = connection.participantFrom.companyId
            buyerId = connection.participantTo.companyId
            buyerName = buyerRead.data.name
            sellerName = sellerRead.data.name
            location = buyerRead.data.location
            buyerRoleId = connection.participantTo.companyRole.id
            sellerRoleId = connection.participantFrom.companyRole.id
            industryId = UUID.fromString(buyerRead.data.industry!!.id)
            verificationStage = ConnectionReadEntity.ConnectionStatus.values()[connection.status.ordinal]
            created = if(System.getenv("spring_profiles_active") == "dev" || System.getenv("spring_profiles_active") == "local") randomDate(2016, 2021) else LocalDate.now()
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
                        serviceReadEntity.get().let {
                            ConnectionReadEntity.Service(
                                    id = it.id,
                                    name = it.name,
                                    slug = it.data.slug,
                                    endDate = connectionService.endDate,
                                    startDate = connectionService.startDate,
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
                        logo = buyerRead.data.logo,
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
                                type = ConnectionRoleReadEntity.RoleType.values()[connection.participantTo.companyRole.type.ordinal],
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
                                id = connection.participantFrom.companyRole.id,
                                name = connection.participantFrom.companyRole.name,
                                type = ConnectionRoleReadEntity.RoleType.values()[connection.participantFrom.companyRole.type.ordinal],
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

    fun randomDate(startYear: Int, endYear: Int): LocalDate {
        val day: Int = Random.nextInt(1, 28)
        val month: Int = Random.nextInt(1, 12)
        val year: Int = Random.nextInt(startYear, endYear)
        return LocalDate.of(year, month, day)
    }
}


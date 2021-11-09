package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.dataloader.DataLoader
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.updater.handler.connection.Connection
import com.briolink.companyservice.updater.handler.connection.ConnectionCompanyRole
import com.briolink.companyservice.updater.handler.connection.ConnectionParticipant
import com.briolink.companyservice.updater.handler.connection.ConnectionService
import com.briolink.companyservice.updater.handler.connection.ConnectionStatus
import com.briolink.companyservice.updater.handler.connection.ConnectionCompanyRoleType
import com.briolink.companyservice.updater.handler.connection.ConnectionHandlerService
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.Year
import java.util.*
import kotlin.random.Random

@Component
@Order(3)
class ConnectionDataLoader(
    private var connectionReadRepository: ConnectionReadRepository,
    private var userReadRepository: UserReadRepository,
    private var userJobPositionReadRepository: UserJobPositionReadRepository,
    private var companyReadRepository: CompanyReadRepository,
    private var serviceReadRepository: ServiceReadRepository,
    private var connectionServiceHandler: ConnectionHandlerService,
) : DataLoader() {
    override fun loadData() {
        if (
            connectionReadRepository.count().toInt() == 0 &&
            userReadRepository.count().toInt() != 0 &&
            serviceReadRepository.count().toInt() != 0 &&
            companyReadRepository.count().toInt() != 0 &&
            userJobPositionReadRepository.count().toInt() != 0
        ) {
            val listCompany = companyReadRepository.findAll()
            val listUser = userReadRepository.findAll()
            val listConnectionRole = listOf(
                    ConnectionCompanyRole(UUID.randomUUID(), "Customer", ConnectionCompanyRoleType.Seller),
                    ConnectionCompanyRole(UUID.randomUUID(), "Supplier", ConnectionCompanyRoleType.Buyer),
                    ConnectionCompanyRole(UUID.randomUUID(), "Investor", ConnectionCompanyRoleType.Seller),
                    ConnectionCompanyRole(UUID.randomUUID(), "Investor", ConnectionCompanyRoleType.Buyer),
                    ConnectionCompanyRole(UUID.randomUUID(), "Client", ConnectionCompanyRoleType.Buyer),
                    ConnectionCompanyRole(UUID.randomUUID(), "Vendor", ConnectionCompanyRoleType.Seller),
            )
            val listService = serviceReadRepository.findAll()
            val connectionStatusList = listOf(ConnectionStatus.Verified, ConnectionStatus.Pending, ConnectionStatus.InProgress)
            for (i in 1..COUNT_CONNECTION) {
                val from = listCompany.random()
                val to = listCompany.random().let {
                    if (it.id == from.id) listCompany.random().let {
                        if (it.id == from.id) listCompany.random() else it
                    } else it
                }
                val services = mutableListOf<ConnectionService>()
                for (j in 0..Random.nextInt(1, 4)) {
                    services.add(
                            listService.shuffled().find { service -> service.companyId == from.id }!!.let {
                                ConnectionService(
                                        id = it.id,
                                        serviceId = it.id,
                                        serviceName = it.name,
                                        startDate = Year.of(Random.nextInt(2010, 2021)),
                                        endDate = if (Random.nextBoolean()) null else Year.of(Random.nextInt(2010, 2020)),
                                )
                            },
                    )
                }
                connectionServiceHandler.createOrUpdate(
                        Connection(
                                id = UUID.randomUUID(),
                                participantFrom = ConnectionParticipant(
                                        userId = listUser.random().id,
                                        userJobPositionTitle = null,
                                        companyId = from.id,
                                        companyRole = listConnectionRole.shuffled()
                                                .find { connectionCompanyRole -> connectionCompanyRole.type == ConnectionCompanyRoleType.Seller }!!,
                                ),
                                participantTo = ConnectionParticipant(
                                        userId = listUser.random().id,
                                        userJobPositionTitle = null,
                                        companyId = to.id,
                                        companyRole = listConnectionRole.shuffled()
                                                .find { connectionCompanyRole -> connectionCompanyRole.type == ConnectionCompanyRoleType.Buyer }!!,
                                ),
                                services = ArrayList(services),
                                status = connectionStatusList.random(),
                                created = randomInstant(2010, 2020),
                        ),
                )
            }
        }
    }

    companion object {
        const val COUNT_CONNECTION = 2000
    }
}

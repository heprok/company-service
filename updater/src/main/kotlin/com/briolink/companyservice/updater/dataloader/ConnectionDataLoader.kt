package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.dataloader.DataLoader
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.companyservice.updater.handler.project.ProjectCompanyRole
import com.briolink.companyservice.updater.handler.project.ProjectCompanyRoleType
import com.briolink.companyservice.updater.handler.project.ProjectEventData
import com.briolink.companyservice.updater.handler.project.ProjectHandlerService
import com.briolink.companyservice.updater.handler.project.ProjectParticipant
import com.briolink.companyservice.updater.handler.project.ProjectService
import com.briolink.companyservice.updater.handler.project.ProjectStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.Year
import java.util.UUID
import kotlin.random.Random

@Component
@Order(3)
class ConnectionDataLoader(
    private var connectionReadRepository: ConnectionReadRepository,
    private var userReadRepository: UserReadRepository,
    private var userJobPositionReadRepository: UserJobPositionReadRepository,
    private var companyReadRepository: CompanyReadRepository,
    private var serviceReadRepository: ServiceReadRepository,
    private var connectionServiceHandler: ProjectHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher
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
                ProjectCompanyRole(UUID.randomUUID(), "Customer", ProjectCompanyRoleType.Seller),
                ProjectCompanyRole(UUID.randomUUID(), "Supplier", ProjectCompanyRoleType.Buyer),
                ProjectCompanyRole(UUID.randomUUID(), "Investor", ProjectCompanyRoleType.Seller),
                ProjectCompanyRole(UUID.randomUUID(), "Investor", ProjectCompanyRoleType.Buyer),
                ProjectCompanyRole(UUID.randomUUID(), "Client", ProjectCompanyRoleType.Buyer),
                ProjectCompanyRole(UUID.randomUUID(), "Vendor", ProjectCompanyRoleType.Seller),
            )
            val listService = serviceReadRepository.findAll()
            val connectionStatusList =
                listOf(ProjectStatus.Verified, ProjectStatus.Pending, ProjectStatus.InProgress)
            for (i in 1..COUNT_CONNECTION) {
                val from = listCompany.random()
                val to = listCompany.random().let {
                    if (it.id == from.id) listCompany.random().let {
                        if (it.id == from.id) listCompany.random() else it
                    } else it
                }
                val services = mutableListOf<ProjectService>()
                for (j in 0..Random.nextInt(1, 6)) {
                    val startYear = Year.of(Random.nextInt(2010, 2021))
                    val endYear = Year.of(Random.nextInt(startYear.value, 2021))
                    services.add(
                        listService.shuffled().find { service -> service.companyId == from.id }!!.let {
                            ProjectService(
                                id = UUID.randomUUID(),
                                serviceId = if (Random.nextBoolean()) it.id else null,
                                serviceName = it.name,
                                startDate = startYear,
                                endDate = if (Random.nextBoolean()) null else endYear,
                            )
                        },
                    )
                }
                connectionServiceHandler.createOrUpdate(
                    ProjectEventData(
                        id = UUID.randomUUID(),
                        participantFrom = ProjectParticipant(
                            userId = listUser.random().id,
                            userJobPositionTitle = "Developer",
                            companyId = from.id,
                            companyRole = listConnectionRole.shuffled()
                                .find { connectionCompanyRole -> connectionCompanyRole.type == ProjectCompanyRoleType.Seller }!!,
                        ),
                        participantTo = ProjectParticipant(
                            userId = listUser.random().id,
                            userJobPositionTitle = "developer",
                            companyId = to.id,
                            companyRole = listConnectionRole.shuffled()
                                .find { connectionCompanyRole -> connectionCompanyRole.type == ProjectCompanyRoleType.Buyer }!!,
                        ),
                        services = ArrayList(services),
                        status = connectionStatusList.random(),
                        created = randomInstant(2010, 2020),
                    ),
                )
            }
            listCompany.forEach {
                applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(it.id, false))
            }
        }
    }

    companion object {
        const val COUNT_CONNECTION = 500
    }
}

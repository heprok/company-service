package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.dataloader.DataLoader
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ProjectReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.updater.handler.project.ProjectCompanyRoleData
import com.briolink.companyservice.updater.handler.project.ProjectCompanyRoleType
import com.briolink.companyservice.updater.handler.project.ProjectEventData
import com.briolink.companyservice.updater.handler.project.ProjectHandlerService
import com.briolink.companyservice.updater.handler.project.ProjectParticipantData
import com.briolink.companyservice.updater.handler.project.ProjectServiceData
import com.briolink.companyservice.updater.handler.project.ProjectStatus
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.random.Random

@Component
@Order(3)
class ProjectDataLoader(
    private var projectReadRepository: ProjectReadRepository,
    private var userReadRepository: UserReadRepository,
    private var userJobPositionReadRepository: UserJobPositionReadRepository,
    private var companyReadRepository: CompanyReadRepository,
    private var serviceReadRepository: ServiceReadRepository,
    private var projectServiceHandler: ProjectHandlerService,
) : DataLoader() {
    override fun loadData() {
        if (
            projectReadRepository.count().toInt() == 0 &&
            userReadRepository.count().toInt() != 0 &&
            serviceReadRepository.count().toInt() != 0 &&
            companyReadRepository.count().toInt() != 0 &&
            userJobPositionReadRepository.count().toInt() != 0
        ) {
            val listCompany = companyReadRepository.findAll()
            val listUser = userReadRepository.findAll()
            val listProjectRole = listOf(
                ProjectCompanyRoleData(UUID.randomUUID(), "Customer", ProjectCompanyRoleType.Seller),
                ProjectCompanyRoleData(UUID.randomUUID(), "Supplier", ProjectCompanyRoleType.Buyer),
                ProjectCompanyRoleData(UUID.randomUUID(), "Investor", ProjectCompanyRoleType.Seller),
                ProjectCompanyRoleData(UUID.randomUUID(), "Client", ProjectCompanyRoleType.Buyer),
                ProjectCompanyRoleData(UUID.randomUUID(), "Vendor", ProjectCompanyRoleType.Seller),
            )
            val listService = serviceReadRepository.findAll()
            val projectStageList =
                listOf(ProjectStatus.Verified, ProjectStatus.Pending, ProjectStatus.InProgress)

            for (i in 1..COUNT_PROJECT) {
                val from = listCompany.random()
                val to = listCompany.random().let {
                    if (it.id == from.id) listCompany.random().let {
                        if (it.id == from.id) listCompany.random() else it
                    } else it
                }

                val service = listService.shuffled().find { service -> service.companyId == from.id }!!.let {
                    ProjectServiceData(
                        serviceId = if (Random.nextBoolean()) it.id else null,
                        serviceName = it.name,
                        startDate = randomDate(2012, 2018),
                        endDate = if (Random.nextBoolean()) null else randomDate(2019, 2022),
                    )
                }

                projectServiceHandler.createOrUpdate(
                    ProjectEventData(
                        id = UUID.randomUUID(),
                        participantFrom = ProjectParticipantData(
                            userId = listUser.random().id,
                            companyId = from.id,
                            companyRole = listProjectRole.shuffled()
                                .find { connectionCompanyRole -> connectionCompanyRole.type == ProjectCompanyRoleType.Seller }!!,
                        ),
                        participantTo = ProjectParticipantData(
                            userId = listUser.random().id,
                            companyId = to.id,
                            companyRole = listProjectRole.shuffled()
                                .find { connectionCompanyRole -> connectionCompanyRole.type == ProjectCompanyRoleType.Buyer }!!,
                        ),
                        service = service,
                        status = projectStageList.random(),
                    ),
                )
            }
            projectServiceHandler.refreshStatistic(listCompany.map { it.id })
        }
    }

    companion object {
        const val COUNT_PROJECT = 500
    }
}

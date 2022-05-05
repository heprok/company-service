package com.briolink.companyservice.updater.handler.statistic

import com.briolink.companyservice.common.domain.v1_0.CompanyStatistic
import com.briolink.companyservice.common.domain.v1_0.CompanyStatisticConnection
import com.briolink.companyservice.common.domain.v1_0.ProjectRoleData
import com.briolink.companyservice.common.domain.v1_0.ProjectRoleType
import com.briolink.companyservice.common.event.v1_0.CompanyStatisticConnectionEvent
import com.briolink.companyservice.common.event.v1_0.CompanyStatisticEvent
import com.briolink.companyservice.common.jpa.enumeration.CompanyRoleTypeEnum
import com.briolink.companyservice.common.jpa.enumeration.ConnectionObjectTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.ProjectReadEntity
import com.briolink.companyservice.common.jpa.read.entity.statistic.Chart
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartDataList
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartItem
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartList
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItem
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItemWithMarketSegmentAndLocation
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItemWithRoleAndProject
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItemWithVerifyUsed
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartTabItem
import com.briolink.companyservice.common.jpa.read.entity.statistic.StatisticReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ProjectReadRepository
import com.briolink.companyservice.common.jpa.read.repository.StatisticReadRepository
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.companyservice.updater.RefreshStatisticConnectionByCompanyId
import com.briolink.lib.event.publisher.EventPublisher
import com.vladmihalcea.hibernate.type.range.Range
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener
import java.time.Year
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Service
@EnableAsync
class StatisticHandlerService(
    private val statisticReadRepository: StatisticReadRepository,
    private val projectReadRepository: ProjectReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val eventPublisher: EventPublisher,
) {
    @Async
    @Transactional
    fun refreshByCompanyId(companyId: UUID) {
        deleteStatisticByCompanyId(companyId)

        var companyStatistic = StatisticReadEntity(companyId)

        var pageProjects: Page<ProjectReadEntity>? = null

        val servicesProvidedIds = mutableSetOf<UUID>()
        val companyInRoles = mutableSetOf<ProjectReadEntity.ProjectRole>()

        do {
            pageProjects = projectReadRepository.findNotHiddenByCompanyId(
                companyId, pageProjects?.nextPageable() ?: Pageable.ofSize(1000)
            )

            pageProjects.content.forEach { project ->
                val collaboratorParticipant = project.getCollaboratorParticipant(companyId)
                val participant = project.getParticipant(companyId)
                companyInRoles.add(
                    ProjectReadEntity.ProjectRole(
                        id = participant.role.id,
                        name = participant.role.name,
                        type = participant.role.type
                    )
                )
                val collaboratorCompany = companyReadRepository.findById(collaboratorParticipant.companyId)
                    .orElseThrow { throw EntityNotFoundException("Company ${collaboratorParticipant.companyId} not found") }

                // chart data by country
                val country = collaboratorCompany.data.location?.country?.name
                if (!country.isNullOrBlank()) {
                    companyStatistic.chartByCountryData.data.getOrPut(country) { ChartDataList(country, mutableListOf()) }
                        .also { list ->
                            when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.companyId }) {
                                -1 -> list.items.add(
                                    ChartListItemWithMarketSegmentAndLocation(
                                        companyId = collaboratorParticipant.companyId,
                                        verifiedProjects = 1
                                    ),
                                )
                                else -> list.items[i].verifiedProjects = list.items[i].verifiedProjects.inc()
                            }
                        }
                }

                companyStatistic.chartByCountry = getChart(companyStatistic.chartByCountryData)

                // chart data by industry
                val industryName = collaboratorCompany.data.industry?.name

                if (!industryName.isNullOrBlank()) {
                    companyStatistic.chartByIndustryData.data.getOrPut(industryName) { ChartDataList(industryName, mutableListOf()) }
                        .also { list ->
                            when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.companyId }) {
                                -1 -> list.items.add(
                                    ChartListItemWithMarketSegmentAndLocation(
                                        companyId = collaboratorParticipant.companyId,
                                        verifiedProjects = 1
                                    ),
                                )
                                else -> list.items[i].verifiedProjects = list.items[i].verifiedProjects.inc()
                            }
                        }
                }

                companyStatistic.chartByIndustry = getChart(companyStatistic.chartByIndustryData)

                val projectService = project.data.projectService

                // chart data by services provided
                if (collaboratorParticipant.role.type == CompanyRoleTypeEnum.Buyer) {
                    servicesProvidedIds.add(projectService.service.id)
                    if (projectService.service.slug != "-1") {
                        val serviceId = projectService.service.id.toString()
                        val serviceName = projectService.service.name
                        companyStatistic.chartByServicesProvidedData.data.getOrPut(serviceId) {
                            ChartDataList(serviceName, mutableListOf())
                        }.also { list ->
                            when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.companyId }) {
                                -1 -> list.items.add(
                                    ChartListItemWithVerifyUsed(
                                        companyId = collaboratorParticipant.companyId,
                                        verifyUsed = 1,
                                    ),
                                )
                                else -> list.items[i].verifyUsed = list.items[i].verifyUsed.inc()
                            }
                            // serviceReadRepository.findByIdOrNull(UUID.fromString(serviceId))?.apply {
                            //     verifiedUses =
                            //         companyStatistic.chartByServicesProvidedData.data[serviceId]!!.items.sumOf { it.verifyUses }
                            //     LocalDate.of(projectService.endDate?.value ?: Year.now().value, 1, 1).also {
                            //         lastUsed =
                            //             if (lastUsed == null) it
                            //             else if (lastUsed!! < it) it
                            //             else lastUsed
                            //     }
                            //     serviceReadRepository.save(this)
                            // }
                        }
                    }
                }

                companyStatistic.chartByServicesProvided = getChart(
                    companyStatistic.chartByServicesProvidedData,
                    { it.second.items.sumOf { i -> i.verifyUsed } },
                    true,
                )

                // chart data by active projects

                val rangeYearDateService = Range.closed(projectService.startDate.year, projectService.endDate?.year ?: Year.now().value)

                for (year in rangeYearDateService.lower()..rangeYearDateService.upper()) {
                    companyStatistic.chartActiveProjectsByYearData.data.getOrPut(year.toString()) {
                        ChartDataList(year.toString(), mutableListOf())
                    }.also { list ->
                        list.items.add(
                            ChartListItemWithRoleAndProject(
                                companyId = collaboratorParticipant.companyId,
                                serviceName = projectService.service.name,
                                role = collaboratorParticipant.role.name,
                                startDate = projectService.startDate,
                                endDate = projectService.endDate,
                            )
                        )
                    }
                }

                companyStatistic.chartActiveProjectByYear = getChart(
                    companyStatistic.chartActiveProjectsByYearData,
                    {
                        it.second.items.count()
                    },
                    withoutOther = true,
                ) { it.sortedBy { el -> el.first.toInt() } }

                // chart data by new project
                companyStatistic.chartNewProjectsByYearData.data.getOrPut(projectService.startDate.year.toString()) {
                    ChartDataList(projectService.startDate.year.toString(), mutableListOf())
                }.also { list ->
                    list.items.add(
                        ChartListItemWithRoleAndProject(
                            companyId = collaboratorParticipant.companyId,
                            serviceName = projectService.service.name,
                            role = collaboratorParticipant.role.name,
                            startDate = projectService.startDate,
                            endDate = projectService.endDate,
                        )
                    )
                }

                companyStatistic.chartNewProjectByYear = getChart(
                    companyStatistic.chartNewProjectsByYearData,
                    {
                        it.second.items.count()
                    },
                    withoutOther = true,
                ) { it.sortedBy { el -> el.first.toInt() } }
            }
        } while (pageProjects!!.hasNext())

        companyStatistic.totalProjects = pageProjects.totalElements.toInt()
        companyStatistic = setTotalConnection(companyStatistic)
        companyStatistic.totalServicesProvided = servicesProvidedIds.count()

        publishStats(companyStatistic, companyInRoles.toList())
        statisticReadRepository.save(companyStatistic)
    }

    fun deleteStatisticByCompanyId(companyId: UUID) {
        statisticReadRepository.deleteByCompanyId(companyId)
    }

    private fun setTotalConnection(statistic: StatisticReadEntity): StatisticReadEntity =
        statistic.apply {
            totalConnectedPeoples =
                connectionReadRepository.getCountNotHiddenByCompanyAndCollabType(statistic.companyId, ConnectionObjectTypeEnum.User.value)
            totalConnectedCompanies =
                connectionReadRepository.getCountNotHiddenByCompanyAndCollabType(
                    statistic.companyId,
                    ConnectionObjectTypeEnum.Company.value
                )
        }

    @Async
    @TransactionalEventListener
    @Transactional
    fun updateByCompanyId(event: RefreshStatisticByCompanyId) {
        refreshByCompanyId(event.companyId)
        if (event.isUpdateCollaborating) {
            val limit = 5000
            var offset = 0
            while (true) {
                val companyIds =
                    projectReadRepository.getCompanyIdsByCompanyId(event.companyId.toString(), limit, offset)
                if (companyIds.isEmpty()) break
                companyIds.forEach {
                    refreshByCompanyId(it.companyId)
                }
                offset += limit
            }
        }
    }

    @Async
    @TransactionalEventListener
    @Transactional
    fun updateConnectionByCompanyId(event: RefreshStatisticConnectionByCompanyId) {
        var statistic = statisticReadRepository.findByCompanyId(event.companyId)
        if (statistic == null) {
            refreshByCompanyId(event.companyId)
            return
        }

        statistic = setTotalConnection(statistic)
        statisticReadRepository.save(statistic)
        publishStats(statistic)
    }

    private fun publishStats(statistic: StatisticReadEntity, listCompanyRole: List<ProjectReadEntity.ProjectRole>? = null) {
        if (listCompanyRole != null) {

            eventPublisher.publishAsync(
                CompanyStatisticEvent(
                    CompanyStatistic(
                        companyId = statistic.companyId,
                        numberOfVerifications = statistic.totalProjects,
                        companyRoles = listCompanyRole.map {
                            ProjectRoleData(
                                id = it.id,
                                name = it.name,
                                type = ProjectRoleType.valueOf(it.type.name)
                            )
                        } as ArrayList<ProjectRoleData>,
                        connectedPeoples = statistic.totalConnectedPeoples,
                        connectedCompanies = statistic.totalConnectedCompanies,
                        totalConnections = statistic.totalConnections,
                        servicesProvided = statistic.totalServicesProvided,
                        totalProjects = statistic.totalProjects
                    )
                )
            )
        } else {
            eventPublisher.publishAsync(
                CompanyStatisticConnectionEvent(
                    CompanyStatisticConnection(
                        companyId = statistic.companyId,
                        connectedPeoples = statistic.totalConnectedPeoples,
                        connectedCompanies = statistic.totalConnectedCompanies,
                        totalConnections = statistic.totalConnections,
                    )
                )
            )
        }
    }

    fun <T : ChartListItem> getChart(
        chartList: ChartList<T>,
        itemValueSelector: ((Pair<String, ChartDataList<T>>) -> Int) = { it.second.items.size },
        withoutOther: Boolean = false,
        sortSelector: ((List<Pair<String, ChartDataList<T>>>) -> List<Pair<String, ChartDataList<T>>>)? = null
    ): Chart {
        val tabs = mutableListOf<ChartTabItem>()
        val items = mutableListOf<ChartItem>()

        val sortedChartListData =
            if (sortSelector != null)
                sortSelector(chartList.data.toList())
            else
                chartList.data.toList().sortedByDescending { itemValueSelector(it) }

        var otherIndex = -1

        sortedChartListData.forEachIndexed { index, pair ->
            tabs.add(
                ChartTabItem(
                    id = pair.first,
                    name = pair.second.name,
                    total = pair.second.items.size,
                ),
            )

            if (index < COUNT_MAX_VIEW_CHART || withoutOther) {
                val companyIds = pair.second.items
                    .toSortedSet(compareBy { v -> v.companyId })
                    .take(COUNT_COMPANY_ON_HINT).map { it.companyId }.toMutableSet()

                items.add(
                    ChartItem(
                        key = pair.first,
                        name = pair.second.name,
                        value = itemValueSelector(pair),
                        companyIds = companyIds,
                    ),
                )
            } else {
                if (otherIndex == -1) {
                    items.add(ChartItem(key = OTHER_KEY, name = OTHER_NAME, companyIds = mutableSetOf(), value = 0))
                    otherIndex = items.lastIndex
                }

                items[otherIndex] = items[otherIndex].apply {
                    if (companyIds.size < COUNT_COMPANY_ON_HINT) {
                        companyIds.addAll(
                            pair.second.items.toSortedSet(compareBy { v -> v.companyId }).take(COUNT_COMPANY_ON_HINT)
                                .map { it.companyId },
                        )
                        companyIds = companyIds.take(COUNT_COMPANY_ON_HINT).toMutableSet()
                    }
                    this.value = this.value.plus(pair.second.items.size)
                }
            }
        }

        return Chart(items = items, tabs = tabs)
    }

    companion object {
        const val COUNT_MAX_VIEW_CHART = 5
        const val COUNT_COMPANY_ON_HINT = 3
        const val OTHER_KEY = "-1"
        const val OTHER_NAME = "Other"
    }
}

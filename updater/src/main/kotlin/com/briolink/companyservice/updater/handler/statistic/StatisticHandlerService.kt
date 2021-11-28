package com.briolink.companyservice.updater.handler.statistic

import com.briolink.companyservice.common.jpa.enumeration.CompanyRoleTypeEnum
import com.briolink.companyservice.common.jpa.enumeration.ConnectionStatusEnum
import com.briolink.companyservice.common.jpa.read.entity.statistic.Chart
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartDataList
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartItem
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartList
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItem
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItemWithRoles
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItemWithServicesCount
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItemWithUsesCount
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartTabItem
import com.briolink.companyservice.common.jpa.read.entity.statistic.StatisticReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.StatisticReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.util.UUID

@Transactional
@Service
class StatisticHandlerService(
    private val statisticReadRepository: StatisticReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val companyReadRepository: CompanyReadRepository,
) {
    fun refreshByCompanyId(companyId: UUID) {
        deleteStatisticByCompanyId(companyId)
        val companyStatistic = StatisticReadEntity(companyId)
//        val companyStatistic = statisticReadRepository.findByCompanyId(companyId) ?: StatisticReadEntity(companyId)

        val list = connectionReadRepository.getByCompanyIdAndStatusAndNotHiddenOrNotDeleted(companyId, ConnectionStatusEnum.Verified.value)
        companyStatistic.totalConnections = list.count()
        list.forEach { connectionReadEntity ->
            val collaboratorParticipant = if (connectionReadEntity.participantFromCompanyId == companyId)
                connectionReadEntity.data.participantTo else connectionReadEntity.data.participantFrom

            val companyBuyer = if (connectionReadEntity.participantFromRoleType == CompanyRoleTypeEnum.Buyer)
                companyReadRepository.findById(connectionReadEntity.participantFromCompanyId).get()
            else
                companyReadRepository.findById(connectionReadEntity.participantToCompanyId).get()
            // chart data by country
            val country = companyBuyer.data.location?.country?.name
            if (!country.isNullOrBlank()) {
                companyStatistic.chartByCountryData.data.getOrPut(country) { ChartDataList(country, mutableListOf()) }
                    .also { list ->
                        when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.company.id }) {
                            -1 -> list.items.add(
                                ChartListItemWithRoles(
                                    companyId = collaboratorParticipant.company.id,
                                    roles = mutableSetOf(collaboratorParticipant.companyRole.name),
                                ),
                            )
                            else -> list.items[i].roles.add(collaboratorParticipant.companyRole.name)
                        }
                    }
            }
            companyStatistic.chartByCountry = getChart(companyStatistic.chartByCountryData)

            // chart data by industry
            val industry = connectionReadEntity.data.industry
            if (!industry.isNullOrBlank()) {
                companyStatistic.chartByIndustryData.data.getOrPut(industry) { ChartDataList(industry, mutableListOf()) }
                    .also { list ->
                        when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.company.id }) {
                            -1 -> list.items.add(
                                ChartListItemWithRoles(
                                    companyId = collaboratorParticipant.company.id,
                                    mutableSetOf(collaboratorParticipant.companyRole.name),
                                ),
                            )
                            else -> list.items[i].roles.add(collaboratorParticipant.companyRole.name)
                        }
                    }
            }
            companyStatistic.chartByIndustry = getChart(companyStatistic.chartByIndustryData)

            // chart data by services provided
            connectionReadEntity.data.services.forEach { service ->
                if (service.serviceId == null) return@forEach
                val serviceId = service.serviceId.toString()
                val serviceName = service.serviceName
                companyStatistic.chartByServicesProvidedData.data.getOrPut(serviceId) {
                    ChartDataList(serviceName, mutableListOf())
                }.also { list ->
                    when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.company.id }) {
                        -1 -> list.items.add(
                            ChartListItemWithUsesCount(
                                companyId = collaboratorParticipant.company.id,
                                usesCount = 1,
                            ),
                        )
                        else -> list.items[i].usesCount = list.items[i].usesCount.inc()
                    }
                    serviceReadRepository.findByIdOrNull(UUID.fromString(serviceId))?.apply {
                        verifiedUses = companyStatistic.chartByServicesProvidedData.data[serviceId]!!.items.size
                        lastUsed = connectionReadEntity.created.atZone(ZoneId.systemDefault()).toLocalDate()
                        serviceReadRepository.save(this)
                    }
                }
            }
            companyStatistic.chartByServicesProvided = getChart(
                companyStatistic.chartByServicesProvidedData,
                { it.second.items.sumOf { i -> i.usesCount } },
                true,
            )
            companyStatistic.totalServicesProvided = companyStatistic.chartByServicesProvidedData.data.size
            // chart data by number of connections
            val createdYear = connectionReadEntity.created.atZone(ZoneId.systemDefault()).year.toString()
            companyStatistic.chartConnectionCountByYearData.data.getOrPut(createdYear) {
                ChartDataList(createdYear, mutableListOf())
            }.also { list ->
                when (
                    val i = list.items.indexOfFirst {
                        it.companyId == collaboratorParticipant.company.id &&
                            it.companyRole == collaboratorParticipant.companyRole.name
                    }
                ) {
                    -1 -> list.items.add(
                        ChartListItemWithServicesCount(
                            companyId = collaboratorParticipant.company.id,
                            companyRole = collaboratorParticipant.companyRole.name,
                            companyRoleType = collaboratorParticipant.companyRole.type,
                            servicesCount = connectionReadEntity.serviceIds.size,
                        ),
                    )
                    else -> list.items[i].servicesCount = list.items[i].servicesCount.plus(connectionReadEntity.serviceIds.size)
                }
            }
            companyStatistic.totalCollaborationCompanies = companyStatistic
                .chartConnectionCountByYear.items.map { it.companyIds }.flatten().toSet().count()
            companyStatistic.totalConnections =
                companyStatistic.chartConnectionCountByYearData.data.values.sumOf { year -> year.items.size }
            companyStatistic.chartConnectionCountByYear = getChart(
                companyStatistic.chartConnectionCountByYearData,
                {
                    it.second.items.count()
                },
                withoutOther = true,
            ) { it.sortedBy { el -> el.first.toInt() } }
        }
        statisticReadRepository.save(companyStatistic)
    }

    fun deleteStatisticByCompanyId(companyId: UUID) {
        statisticReadRepository.deleteByCompanyId(companyId)
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

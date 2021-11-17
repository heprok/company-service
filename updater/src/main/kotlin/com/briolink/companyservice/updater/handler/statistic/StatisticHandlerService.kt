package com.briolink.companyservice.updater.handler.statistic

import com.briolink.companyservice.common.jpa.enumration.ConnectionStatusEnum
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.util.*

@Transactional
@Service
class StatisticHandlerService(
    private val statisticReadRepository: StatisticReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyReadRepository: CompanyReadRepository,
) {
    fun refreshByCompanyId(companyId: UUID) {
        deleteStatisticByCompanyId(companyId)
        val companyStatistic = statisticReadRepository.findByCompanyId(companyId) ?: StatisticReadEntity(companyId)

        connectionReadRepository.getByCompanyIdAndStatus(companyId, ConnectionStatusEnum.Verified.value).forEach { connectionReadEntity ->
//            addConnectionToStats(it, companyId)
            if (connectionReadEntity.status == ConnectionStatusEnum.Verified) {
                val collaboratorParticipant = if (connectionReadEntity.participantFromCompanyId == companyId)
                    connectionReadEntity.data.participantTo else connectionReadEntity.data.participantFrom

                // chart data by country
                val country = connectionReadEntity.country
                if (!country.isNullOrBlank()) {
                    companyStatistic.chartByCountryData.data.getOrPut(country) { ChartDataList(country, mutableListOf()) }.also { list ->
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
                    companyStatistic.chartByIndustryData.data.getOrPut(industry) { ChartDataList(industry, mutableListOf()) }.also { list ->
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
                    }
                }
                companyStatistic.chartByServicesProvided = getChart(companyStatistic.chartByServicesProvidedData, true)

                // chart data by industry
                val createdYear = connectionReadEntity.created.atZone(ZoneId.systemDefault()).year.toString()
                companyStatistic.chartConnectionCountByYearData.data.getOrPut(createdYear) {
                    ChartDataList(createdYear, mutableListOf())
                }.also { list ->
                    when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.company.id }) {
                        -1 -> list.items.add(
                                ChartListItemWithServicesCount(
                                        companyId = collaboratorParticipant.company.id,
                                        companyRole = collaboratorParticipant.companyRole.name,
                                        companyRoleType = collaboratorParticipant.companyRole.type,
                                        servicesCount = connectionReadEntity.serviceIds.size,
                                ),
                        )
                        else -> list.items[i].servicesCount = list.items[i].servicesCount.inc()
                    }
                }
                companyStatistic.totalCollaborationCompanies = companyStatistic.chartConnectionCountByYearData.data.values.map {
                    it.items.map { it.companyId }
                }.toSet().size
                companyStatistic.totalServicesProvided = companyStatistic.chartByServicesProvidedData.data.values.size
                companyStatistic.totalConnections = companyStatistic.totalConnections + 1
            }
            companyStatistic.chartConnectionCountByYear = getChart(
                    companyStatistic.chartConnectionCountByYearData,
                    true,
            ) { it.sortedBy { el -> el.first.toInt() } }
        }
        statisticReadRepository.save(companyStatistic)
    }

    fun deleteStatisticByCompanyId(companyId: UUID) {
        statisticReadRepository.deleteByCompanyId(companyId)
    }

    fun addConnectionToStats(connectionReadEntity: ConnectionReadEntity, companyId: UUID) {
        if (connectionReadEntity.status == ConnectionStatusEnum.Verified) {
            val companyStatistic = statisticReadRepository.findByCompanyId(companyId) ?: StatisticReadEntity(companyId)
            val collaboratorParticipant = if (connectionReadEntity.participantFromCompanyId == companyId)
                connectionReadEntity.data.participantTo else connectionReadEntity.data.participantFrom

            // chart data by country
            val country = connectionReadEntity.country
            if (!country.isNullOrBlank()) {
                companyStatistic.chartByCountryData.data.getOrPut(country) { ChartDataList(country, mutableListOf()) }.also { list ->
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
                companyStatistic.chartByIndustryData.data.getOrPut(industry) { ChartDataList(industry, mutableListOf()) }.also { list ->
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
                }
            }
            companyStatistic.chartByServicesProvided = getChart(companyStatistic.chartByServicesProvidedData, true)

            // chart data by industry
            val createdYear = connectionReadEntity.created.atZone(ZoneId.systemDefault()).year.toString()
            companyStatistic.chartConnectionCountByYearData.data.getOrPut(createdYear) {
                ChartDataList(createdYear, mutableListOf())
            }.also { list ->
                when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.company.id }) {
                    -1 -> list.items.add(
                            ChartListItemWithServicesCount(
                                    companyId = collaboratorParticipant.company.id,
                                    companyRole = collaboratorParticipant.companyRole.name,
                                    companyRoleType = collaboratorParticipant.companyRole.type,
                                    servicesCount = connectionReadEntity.serviceIds.size,
                            ),
                    )
                    else -> list.items[i].servicesCount = list.items[i].servicesCount.inc()
                }
            }
            companyStatistic.chartConnectionCountByYear = getChart(
                    companyStatistic.chartConnectionCountByYearData,
                    true,
            ) { it.sortedBy { el -> el.first.toInt() } }
            statisticReadRepository.save(companyStatistic)
        }
    }

    fun <T : ChartListItem> getChart(
        chartList: ChartList<T>,
        withoutOther: Boolean = false,
        sortSelector: ((List<Pair<String, ChartDataList<T>>>) -> List<Pair<String, ChartDataList<T>>>)? = null
    ): Chart {
        val tabs = mutableListOf<ChartTabItem>()
        val items = mutableListOf<ChartItem>()

        val sortedChartListData =
                if (sortSelector != null)
                    sortSelector(chartList.data.toList())
                else
                    chartList.data.toList().sortedByDescending { it.second.items.count() }

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
                                value = pair.second.items.size,
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


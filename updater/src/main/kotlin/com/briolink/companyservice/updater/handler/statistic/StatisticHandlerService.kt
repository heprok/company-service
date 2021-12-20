package com.briolink.companyservice.updater.handler.statistic

import com.briolink.companyservice.common.domain.v1_0.CompanyStatistic
import com.briolink.companyservice.common.domain.v1_0.ConnectionCompanyRoleData
import com.briolink.companyservice.common.domain.v1_0.ConnectionCompanyRoleType
import com.briolink.companyservice.common.event.v1_0.CompanyStatisticEvent
import com.briolink.companyservice.common.jpa.enumeration.CompanyRoleTypeEnum
import com.briolink.companyservice.common.jpa.enumeration.ConnectionStatusEnum
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
import com.briolink.companyservice.common.jpa.read.repository.ConnectionServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.StatisticReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.event.publisher.EventPublisher
import com.vladmihalcea.hibernate.type.range.Range
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDate
import java.time.Year
import java.util.UUID

@Service
@EnableAsync
class StatisticHandlerService(
    private val statisticReadRepository: StatisticReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val connectionServiceReadRepository: ConnectionServiceReadRepository,
    private val eventPublisher: EventPublisher,
) {
    @Async
    @Transactional
    fun refreshByCompanyId(companyId: UUID) {
        deleteStatisticByCompanyId(companyId)
        val companyStatistic = StatisticReadEntity(companyId)
//        val companyStatistic = statisticReadRepository.findByCompanyId(event.companyId) ?: StatisticReadEntity(event.companyId)

        val list = connectionReadRepository.getByCompanyIdAndStatusAndNotHiddenOrNotDeleted(
            companyId,
            ConnectionStatusEnum.Verified.value
        )
        val collaborationCompanyIds = mutableSetOf<UUID>()
        val servicesProvidedIds = mutableSetOf<UUID>()
        val participantCompanyRole = mutableSetOf<ConnectionReadEntity.CompanyRole>()

        list.forEach { connectionReadEntity ->
            val collaboratorParticipant: ConnectionReadEntity.Participant

            if (connectionReadEntity.participantFromCompanyId == companyId) {
                collaboratorParticipant = connectionReadEntity.data.participantTo
                participantCompanyRole.add(connectionReadEntity.data.participantFrom.companyRole)
            } else {
                collaboratorParticipant = connectionReadEntity.data.participantFrom
                participantCompanyRole.add(connectionReadEntity.data.participantTo.companyRole)
            }

            val collaboratorCompany = companyReadRepository.findById(collaboratorParticipant.company.id).get()
            collaborationCompanyIds.add(collaboratorCompany.id)
            // chart data by country
            val country = collaboratorCompany.data.location?.country?.name
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
            val industryName = collaboratorCompany.data.industry?.name
            if (!industryName.isNullOrBlank()) {
                companyStatistic.chartByIndustryData.data.getOrPut(industryName) {
                    ChartDataList(
                        industryName,
                        mutableListOf()
                    )
                }
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
            if (collaboratorParticipant.companyRole.type == CompanyRoleTypeEnum.Buyer) {
                connectionReadEntity.data.services.forEach { service ->
                    service.serviceId?.let { servicesProvidedIds.add(it) }
                    if (service.serviceId != null && service.slug != "-1") {
                        val serviceId = service.serviceId.toString()
                        val serviceName = service.serviceName
                        companyStatistic.chartByServicesProvidedData.data.getOrPut(serviceId) {
                            ChartDataList(serviceName, mutableListOf())
                        }.also { list ->
                            when (
                                val i =
                                    list.items.indexOfFirst { it.companyId == collaboratorParticipant.company.id }
                            ) {
                                -1 -> list.items.add(
                                    ChartListItemWithUsesCount(
                                        companyId = collaboratorParticipant.company.id,
                                        usesCount = 1,
                                    ),
                                )
                                else -> list.items[i].usesCount = list.items[i].usesCount.inc()
                            }
                            serviceReadRepository.findByIdOrNull(UUID.fromString(serviceId))?.apply {
                                verifiedUses =
                                    companyStatistic.chartByServicesProvidedData.data[serviceId]!!.items.sumOf { it.usesCount }
                                LocalDate.of(service.endDate?.value ?: Year.now().value, 1, 1).also {
                                    lastUsed =
                                        if (lastUsed == null) it
                                        else if (lastUsed!! < it) it
                                        else lastUsed
                                }
                                serviceReadRepository.save(this)
                            }
                        }
                    }
                }
            }

            companyStatistic.chartByServicesProvided = getChart(
                companyStatistic.chartByServicesProvidedData,
                { it.second.items.sumOf { i -> i.usesCount } },
                true,
            )
            // chart data by number of connections

            val minDateService = connectionReadEntity.data.services.minOf { it.startDate.value }
            val maxDateService = connectionReadEntity.data.services.maxOf { it.endDate?.value ?: Year.now().value }
            val rangeYearDateService = Range.closed(minDateService, maxDateService)
            val sellerId = if (connectionReadEntity.participantFromRoleType == CompanyRoleTypeEnum.Seller)
                connectionReadEntity.participantFromCompanyId else connectionReadEntity.participantToCompanyId
            connectionReadEntity.data.services.forEach {
                connectionServiceReadRepository.findByIdOrNull(it.id)?.apply {
                    hidden = connectionReadEntity.hiddenCompanyIds.contains(sellerId)
                    connectionServiceReadRepository.save(this)
                }
            }
//            val createdYear = connectionReadEntity.created.atZone(ZoneId.systemDefault()).year.toString()
            for (year in rangeYearDateService.lower()..rangeYearDateService.upper()) {
                companyStatistic.chartConnectionCountByYearData.data.getOrPut(year.toString()) {
                    ChartDataList(year.toString(), mutableListOf())
                }.also { list ->
//                    when (
//                        val i = list.items.indexOfFirst {
//                            it.companyId == collaboratorParticipant.company.id &&
//                                it.companyRole == collaboratorParticipant.companyRole.name
//                        }
//                    ) {
//                        -1 -> list.items.add(
//                            ChartListItemWithServicesCount(
//                                companyId = collaboratorParticipant.company.id,
//                                companyRole = collaboratorParticipant.companyRole.name,
//                                companyRoleType = collaboratorParticipant.companyRole.type,
//                                servicesCount = connectionReadEntity.serviceIds.size,
//                            ),
//                        )
//                        else -> list.items[i].servicesCount =
//                            list.items[i].servicesCount.plus(connectionReadEntity.serviceIds.size)
//                    }
                    list.items.add(
                        ChartListItemWithServicesCount(
                            companyId = collaboratorParticipant.company.id,
                            companyRole = collaboratorParticipant.companyRole.name,
                            companyRoleType = collaboratorParticipant.companyRole.type,
                            servicesCount = connectionReadEntity.serviceIds.size,
                        )
                    )
                }
            }

            companyStatistic.chartConnectionCountByYear = getChart(
                companyStatistic.chartConnectionCountByYearData,
                {
                    it.second.items.count()
                },
                withoutOther = true,
            ) { it.sortedBy { el -> el.first.toInt() } }
        }

        companyStatistic.totalConnections = list.count()
        companyStatistic.totalCollaborationCompanies = collaborationCompanyIds.count()
        companyStatistic.totalServicesProvided = servicesProvidedIds.count()

        publishStats(companyId, companyStatistic.totalConnections, participantCompanyRole)
        statisticReadRepository.save(companyStatistic)
    }

    fun deleteStatisticByCompanyId(companyId: UUID) {
        statisticReadRepository.deleteByCompanyId(companyId)
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
                    connectionReadRepository.getCompanyIdsByCompanyId(event.companyId.toString(), limit, offset)
                if (companyIds.isEmpty()) break
                companyIds.forEach {
                    refreshByCompanyId(it.companyId)
                }
                offset += limit
            }
        }
    }

    private fun publishStats(
        companyId: UUID,
        numberOfVerifications: Int,
        listCompanyRole: Set<ConnectionReadEntity.CompanyRole>
    ) {
        eventPublisher.publishAsync(
            CompanyStatisticEvent(
                CompanyStatistic(
                    companyId = companyId,
                    numberOfVerifications = numberOfVerifications,
                    companyRoles = listCompanyRole.map {
                        ConnectionCompanyRoleData(
                            id = it.id,
                            name = it.name,
                            type = ConnectionCompanyRoleType.valueOf(it.type.name)
                        )
                    } as ArrayList<ConnectionCompanyRoleData>
                )
            )
        )
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

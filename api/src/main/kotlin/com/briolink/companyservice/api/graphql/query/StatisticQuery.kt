package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.types.ChartByCountry
import com.briolink.companyservice.api.types.ChartByIndustry
import com.briolink.companyservice.api.types.ChartByNumberConnection
import com.briolink.companyservice.api.types.ChartByServicesProvider
import com.briolink.companyservice.api.types.ChartCompany
import com.briolink.companyservice.api.types.Charts
import com.briolink.companyservice.api.types.Image
import com.briolink.companyservice.api.types.TabItemsCount
import com.briolink.companyservice.api.types.Tooltip
import com.briolink.companyservice.common.jpa.read.entity.StatisticReadEntity
import com.briolink.companyservice.common.jpa.read.repository.StatisticReadRepository
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.time.Year
import java.util.UUID
import javax.persistence.EntityNotFoundException

@DgsComponent
class StatisticQuery(private val statisticReadRepository: StatisticReadRepository) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCharts(
        @InputArgument("companyId") companyId: String,
        @InputArgument("offset") offset: Int,
    ): Charts? {
        try {
            val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                    .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }

            val chartByCountry = getChartsByCountry(statistic.statsByCountry)
            val chartByIndustry = getChartsByIndustry(statistic.statsByIndustry)
            val chartByNumberConnection = getChartsNumberConnection(statistic.statsNumberConnection)

            val chartByServiceProvided = getChartsServicesProvided(statistic.statsServiceProvided)
            return Charts(
                    chartByCountry = chartByCountry,
                    chartByIndustry = chartByIndustry,
                    chartByNumberConnection = chartByNumberConnection,
                    chartByServiceProvided = chartByServiceProvided,
            )
        } catch (e: EntityNotFoundException) {
            return null
        }
    }

    private fun getSortingListChart(list: List<Pair<String, Int>>, offset: Int = 3): List<Pair<String, Int>> {
        val sortingListValues = mutableListOf<Int>()
        val sortingListKey = mutableListOf<String>()

        if (offset < list.count()) {
            val otherValue = list.subList(offset, list.count()).sumOf { pair: Pair<String, Int> -> pair.second }
            val otherPair: Pair<String, Int> = Pair("Other", otherValue)
            list.dropLastWhile { pair: Pair<String, Int> -> pair.first == list[offset].first }
            sortingListValues.addAll(list.map { it.second }.plus(otherPair.second))
            sortingListKey.addAll(list.map { it.first }.plus(otherPair.first))
        } else {
            sortingListValues.addAll(list.map { it.second })
            sortingListKey.addAll(list.map { it.first })
        }
        val sortingList = mutableListOf<Pair<String, Int>>()
        sortingListKey.forEachIndexed { index, key ->
            sortingList.add(Pair(key, sortingListValues[index]))
        }
        return sortingList
    }

    private fun getTooltip(companiesStats: MutableMap<String, StatisticReadEntity.CompaniesStats>, offset: Int = 3): MutableList<Tooltip> {
        val tooltipList = mutableListOf<Tooltip>()
        companiesStats.forEach { (key, companies) ->
            val sortingChartCompany = companies.listCompanies.take(offset).map {
                ChartCompany(
                        name = it.name,
                        slug = it.slug,
                        logo = Image(it.logo),
                        id = it.id.toString(),
                )
            }
            tooltipList.add(Tooltip(key = key, value = sortingChartCompany))
        }
        return tooltipList
    }

    private fun getTooltipOnInt(
        companiesStats: MutableMap<Int, StatisticReadEntity.CompaniesStats>,
        offset: Int = 3
    ): MutableList<Tooltip> {
        val tooltipList = mutableListOf<Tooltip>()
        companiesStats.forEach { (key, companies) ->
            val sortingChartCompany = companies.listCompanies.take(offset).map {
                ChartCompany(
                        name = it.name,
                        slug = it.slug,
                        logo = Image(it.logo),
                        id = it.id.toString(),
                )
            }
            tooltipList.add(Tooltip(key = key.toString(), value = sortingChartCompany))
        }
        return tooltipList
    }

    // ============= country stats ================

    fun getChartsByCountry(
        statsByCountry: StatisticReadEntity.StatsByCountry,
        offset: Int = 3
    ): ChartByCountry {
        val statisticByCountry = statsByCountry.totalCountByCountry.toList().sortedByDescending { pair -> pair.second }

        val sortingList = getSortingListChart(statisticByCountry, offset)
        val tooltipList = getTooltip(statsByCountry.companiesStatsByCountry)
        return ChartByCountry(
                values = sortingList.map { it.first },
                data = sortingList.map { it.second },
                tooltip = tooltipList,
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompanyStatsByCountry(
        @InputArgument("companyId") companyId: String,
        @InputArgument("country") country: String
    ): List<ChartCompany>? = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
            .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }.statsByCountry.companiesStatsByCountry[country]?.let { companiesStats ->
                companiesStats.listCompanies.map { ChartCompany.fromEntity(it) }
            }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getTabsStatsByCountry(
        @InputArgument("companyId") companyId: String,
    ): List<TabItemsCount>? = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
            .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }.statsByCountry.totalCountByCountry.let { totalCount ->
                totalCount.map { (country, count) ->
                    TabItemsCount(key = country, count = count)
                }
            }

    // ============= industry stats ================
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompanyStatsByIndustry(
        @InputArgument("companyId") companyId: String,
        @InputArgument("industryName") industryName: String
    ): List<ChartCompany>? = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
            .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }.statsByIndustry.companiesStatsByIndustry[industryName]?.let { companiesStats ->
                companiesStats.listCompanies.map { ChartCompany.fromEntity(it) }
            }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getTabsStatsByIndustry(
        @InputArgument("companyId") companyId: String,
    ): List<TabItemsCount>? = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
            .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }.statsByIndustry.totalCountByIndustry.let { totalCount ->
                totalCount.map { (industryName, count) ->
                    TabItemsCount(key = industryName, count = count)
                }
            }

    private fun getChartsByIndustry(
        statsByIndustry: StatisticReadEntity.StatsByIndustry,
        offset: Int = 3
    ): ChartByIndustry {
        val statisticByIndustry = statsByIndustry.totalCountByIndustry.toList().sortedByDescending { pair -> pair.second }
        val sortingList = getSortingListChart(statisticByIndustry, offset)
        val tooltipList = getTooltip(statsByIndustry.companiesStatsByIndustry)
        return ChartByIndustry(
                values = sortingList.map { it.first },
                data = sortingList.map { it.second },
                tooltip = tooltipList,
        )
    }

    // ============= number connections stats ================


    fun getChartsNumberConnection(
        statsByNumberConnection: StatisticReadEntity.StatsNumberConnection
    ): ChartByNumberConnection {
        return ChartByNumberConnection(
                values = statsByNumberConnection.companiesStatsByYear.map { Year.of(it.key) },
                data = statsByNumberConnection.companiesStatsByYear.values.map {
                    it.totalCount.values.sum()
                },
                tooltip = getTooltipOnInt(statsByNumberConnection.companiesStatsByYear),
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompanyStatsByNumberConnectionOnYear(
        @InputArgument("companyId") companyId: String,
        @InputArgument("year") year: Year
    ): List<ChartCompany>? = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
            .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }.statsNumberConnection.companiesStatsByYear[year.value]?.let { companiesStats ->
                companiesStats.listCompanies.map { ChartCompany.fromEntity(it) }
            }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getTabsStatsByNumberConnection(
        @InputArgument("companyId") companyId: String,
    ): List<TabItemsCount>? = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
            .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }.statsNumberConnection.companiesStatsByYear.let {
                it.map { (year, companiesStats) ->
                    TabItemsCount(key = year.toString(), count = companiesStats.totalCount.values.sum())
                }
            }

    // ============= service provided stats ================

    fun getChartsServicesProvided(
        statsByServiceProvided: StatisticReadEntity.StatsServiceProvided,
        offset: Int = 5,
    ): ChartByServicesProvider {
        val statisticByServicesProvider =
                statsByServiceProvided.totalCountByService.toList().sortedByDescending { pair -> pair.second }
        val sortingList = getSortingListChart(statisticByServicesProvider, offset)
        val tooltipList = getTooltip(statsByServiceProvided.companiesStatsByService)
        return ChartByServicesProvider(
                values = sortingList.map { it.first },
                data = sortingList.map { it.second },
                tooltip = tooltipList,
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompanyStatsByService(
        @InputArgument("companyId") companyId: String,
        @InputArgument("serviceName") serviceName: String
    ): List<ChartCompany>? = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
            .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }.statsServiceProvided.companiesStatsByService[serviceName]?.let { companiesStats ->
                companiesStats.listCompanies.map { ChartCompany.fromEntity(it) }
            }
}

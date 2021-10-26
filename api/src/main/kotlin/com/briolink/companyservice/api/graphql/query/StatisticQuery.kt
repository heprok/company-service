package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromCompaniesStats
import com.briolink.companyservice.api.types.ChartByCountry
import com.briolink.companyservice.api.types.ChartByIndustry
import com.briolink.companyservice.api.types.ChartByNumberConnection
import com.briolink.companyservice.api.types.ChartByServicesProvider
import com.briolink.companyservice.api.types.ChartCompany
import com.briolink.companyservice.api.types.Charts
import com.briolink.companyservice.api.types.GraphicStatsByNumberConnection
import com.briolink.companyservice.api.types.GraphicValueCompany
import com.briolink.companyservice.api.types.Image
import com.briolink.companyservice.api.types.Tooltip
import com.briolink.companyservice.common.jpa.read.entity.StatisticReadEntity
import com.briolink.companyservice.common.jpa.read.repository.StatisticReadRepository
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.apache.logging.log4j.util.Chars
import org.springframework.security.access.prepost.PreAuthorize
import java.time.Year
import java.util.UUID
import javax.persistence.EntityNotFoundException

@DgsComponent
class StatisticQuery(private val statisticReadRepository: StatisticReadRepository) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatistic(
        @InputArgument("companyId") companyId: String,
        @InputArgument("offset") offset: Int,

        ): Charts? {
        try {
            val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                    .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }


            val statsByCountry = statistic.statsByCountry?.let { getStatisticByCountry(it) }
            val statsByIndustry = statistic.statsByIndustry?.let { getStatisticByIndustry(it) }
            val statsByNumberConnection = statistic.statsNumberConnection?.let { getStatisticNumberConnection(it) }

            val statsByServiceProvided = statistic.statsServiceProvided?.let { getStatisticServicesProvided(it) }
            return Charts(
                    statsByCountry = statsByCountry,
                    statsByIndustry = statsByIndustry,
                    statsByNumberConnection = statsByNumberConnection,
                    statsByServiceProvided = statsByServiceProvided,
            )
        } catch (e: EntityNotFoundException) {
            return null
        }
    }

//    private fun getSortingList(list: List<GraphicValueCompany>, limit: Int = 2): MutableList<GraphicValueCompany> {
//        val sortingList = mutableListOf<GraphicValueCompany>()
//        if (limit < list.count()) {
//            val otherValue = list.subList(limit, list.count()).sumOf {
//                it.value
//            }
//            val otherCompanies = list.subList(limit, list.count()).map {
//                it.companies?.distinctBy { graphCompany -> graphCompany?.name }
//            }.let {
//                it.map {
//                    it?.get(0)
//                }
//            }
//
//            sortingList.addAll(list.take(limit))
//            if (list.count() > limit) {
//                sortingList.add(
//                        GraphicValueCompany(
//                                name = "Other",
//                                value = otherValue,
//                                companies = otherCompanies.take(3),
//                        ),
//                )
//            }
//        } else {
//            sortingList.addAll(list)
//        }
//        return sortingList
//    }

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
//        sortingListKey.forEach { country ->
//            val sortingChartCompany = statistic.statsByCountry!!.companiesStatsByCountry[country]?.let { companiesStats ->
//                companiesStats.listCompanies.sortedByDescending { pair -> pair.name }.map {
//                    ChartCompany(
//                            name = it.name,
//                            slug = it.slug,
//                            logo = Image(it.logo),
//                            id = it.id.toString(),
//                    )
//                }
//            }
//            tooltipList.add(Tooltip(key = country, value = sortingChartCompany))
//        }
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
//        if (offset < companiesStats.count())
//            tooltipList.add(
//                    Tooltip(
//                            key = "Other",
//                            value = companiesStats.toList().subList(offset + 1, offset + 1).map {
//                                it.second.listCompanies.map {
//                                    ChartCompany(
//                                            name = it.name,
//                                            slug = it.slug,
//                                            logo = Image(it.logo),
//                                            id = it.id.toString(),
//                                    )
//                                }
//                            },
//                    ),
//            )
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
//        if (offset < companiesStats.count())
//            tooltipList.add(
//                    Tooltip(
//                            key = "Other",
//                            value = companiesStats.toList().subList(offset + 1, offset + 1).map {
//                                it.second.listCompanies.map {
//                                    ChartCompany(
//                                            name = it.name,
//                                            slug = it.slug,
//                                            logo = Image(it.logo),
//                                            id = it.id.toString(),
//                                    )
//                                }
//                            },
//                    ),
//            )
        return tooltipList
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatisticByCountry(
        @InputArgument("companyId") companyId: String,
        @InputArgument("offset") offset: Int,
    ): ChartByCountry? {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }

        val statisticByCountry = statistic.statsByCountry?.totalCountByCountry?.toList()?.sortedByDescending { pair -> pair.second }

        return if (statisticByCountry != null) {
            val sortingList = getSortingListChart(statisticByCountry, offset)
            val tooltipList = getTooltip(statistic.statsByCountry!!.companiesStatsByCountry)
            ChartByCountry(
                    values = sortingList.map { it.first },
                    data = sortingList.map { it.second },
                    tooltip = tooltipList,
            )
        } else {
            null
        }
    }

    fun getStatisticByCountry(
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
    fun getStatisticByIndustry(
        @InputArgument("companyId") companyId: String,
        @InputArgument("offset") offset: Int,
    ): ChartByIndustry? {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }

        val statisticByIndustry = statistic.statsByIndustry?.totalCountByIndustry?.toList()?.sortedByDescending { pair -> pair.second }
        return if (statisticByIndustry != null) {
            val sortingList = getSortingListChart(statisticByIndustry, offset)
            val tooltipList = getTooltip(statistic.statsByIndustry!!.companiesStatsByIndustry)
            ChartByIndustry(
                    values = sortingList.map { it.first },
                    data = sortingList.map { it.second },
                    tooltip = tooltipList,
            )
        } else {
            null
        }
    }

    private fun getStatisticByIndustry(
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

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatisticNumberConnection(
        @InputArgument("companyId") companyId: String,
    ): ChartByNumberConnection? {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }

        val statisticByYear = statistic.statsNumberConnection

        return if (statisticByYear != null) {
            ChartByNumberConnection(
                    values = statisticByYear.companiesStatsByYear.map { Year.of(it.key) },
                    data = statisticByYear.companiesStatsByYear.values.map {
                        it.totalCount.values.sum()
                    },
                    tooltip = getTooltipOnInt(statisticByYear.companiesStatsByYear),
            )
        } else {
            null
        }
    }

    fun getStatisticNumberConnection(
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
    fun getStatisticNumberConnectionOnYear(
        @InputArgument("companyId") companyId: String,
        @InputArgument("year") year: Year
    ): GraphicStatsByNumberConnection {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }
        return GraphicStatsByNumberConnection(
                values = statistic.statsNumberConnection!!.companiesStatsByYear[year.value]?.let {
                    listOf(GraphicValueCompany.fromCompaniesStats(name = year.value.toString(), companiesStats = it, limit = null))
                },
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatisticServicesProvided(
        @InputArgument("companyId") companyId: String,
        @InputArgument("offset") offset: Int,
    ): ChartByServicesProvider? {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }

        val statisticByServicesProvider =
                statistic.statsServiceProvided?.totalCountByService?.toList()?.sortedByDescending { pair -> pair.second }

        return if (statisticByServicesProvider != null) {
            val sortingList = getSortingListChart(statisticByServicesProvider, offset)
            val tooltipList = getTooltip(statistic.statsServiceProvided!!.companiesStatsByService)
            ChartByServicesProvider(
                    values = sortingList.map { it.first },
                    data = sortingList.map { it.second },
                    tooltip = tooltipList,
            )
        } else {
            null
        }
    }

    fun getStatisticServicesProvided(
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
}

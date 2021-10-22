package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromCompaniesStats
import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.types.GraphCompany
import com.briolink.companyservice.api.types.GraphService
import com.briolink.companyservice.api.types.GraphicStatistics
import com.briolink.companyservice.api.types.GraphicStatsByCountry
import com.briolink.companyservice.api.types.GraphicStatsByIndustry
import com.briolink.companyservice.api.types.GraphicStatsByNumberConnection
import com.briolink.companyservice.api.types.GraphicStatsByServiceProvider
import com.briolink.companyservice.api.types.GraphicValueCompany
import com.briolink.companyservice.api.types.GraphicValueService
import com.briolink.companyservice.api.types.Image
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.ServiceList
import com.briolink.companyservice.common.jpa.read.repository.StatisticReadRepository
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.net.URL
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

    ): GraphicStatistics {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }


        val statsByCountry = GraphicStatsByCountry(
                values = statistic.statsByCountry!!.countries.map { country ->
                    GraphicValueCompany.fromCompaniesStats(name = country.key, companiesStats = country.value, limit = 3)
                }.sortedByDescending { graphicValueCompany -> graphicValueCompany.value }.let { list ->
                    getSortingList(list, offset)
                },
        )
        val statsByIndustry = GraphicStatsByIndustry(
                values = statistic.statsByIndustry!!.industries.map { industry ->
                    GraphicValueCompany.fromCompaniesStats(name = industry.key, companiesStats = industry.value, limit = 3)
                }.sortedByDescending { graphicValueCompany -> graphicValueCompany.value }.let { list ->
                    getSortingList(list, offset)
                },
        )
        val statsByNumberConnection = GraphicStatsByNumberConnection(
                values = statistic.statsNumberConnection!!.years.map { year ->
                    GraphicValueCompany.fromCompaniesStats(name = year.key.toString(), companiesStats = year.value, limit = 3)
                },
        )

        val statsByServiceProvided = GraphicStatsByServiceProvider(
                values = statistic.statsServiceProvided!!.services.map {
                    GraphicValueService.fromEntity(it.value)
                }.sortedByDescending { graphicValueService -> graphicValueService.value }.take(offset),
        )
        return GraphicStatistics(
                statsByCountry = statsByCountry,
                statsByIndustry = statsByIndustry,
                statsByNumberConnection = statsByNumberConnection,
                statsByServiceProvided = statsByServiceProvided,
        )
    }

    private fun getSortingList(list: List<GraphicValueCompany>, limit: Int = 2): MutableList<GraphicValueCompany> {
        val sortingList = mutableListOf<GraphicValueCompany>()
        if (limit < list.count()) {
            val otherValue = list.subList(limit, list.count()).sumOf {
                it.value
            }
            val otherCompanies = list.subList(limit, list.count()).map {
                it.companies?.distinctBy { graphCompany -> graphCompany?.name }
            }.let {
                it.map {
                  it?.get(0)
                }
            }

            sortingList.addAll(list.take(limit))
            if (list.count() > limit) {
                sortingList.add(
                        GraphicValueCompany(
                                name = "Other",
                                value = otherValue,
                                companies = otherCompanies.take(3),
                        ),
                )
            }
        } else {
            sortingList.addAll(list)
        }
        return sortingList
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatisticByCountry(
        @InputArgument("companyId") companyId: String,
        @InputArgument("offset") offset: Int,
    ): GraphicStatsByCountry {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }
        return GraphicStatsByCountry(
                values = statistic.statsByCountry!!.countries.map { country ->
                    GraphicValueCompany.fromCompaniesStats(name = country.key.toString(), companiesStats = country.value, limit = 3)
                }.sortedByDescending { graphicValueService -> graphicValueService.value }.take(offset),
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatisticByIndustry(
        @InputArgument("companyId") companyId: String,
        @InputArgument("offset") offset: Int,
    ): GraphicStatsByIndustry {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }
        return GraphicStatsByIndustry(
                values = statistic.statsByIndustry!!.industries.map { industry ->
                    GraphicValueCompany.fromCompaniesStats(name = industry.key.toString(), companiesStats = industry.value, limit = 3)
                }.sortedByDescending { graphicValueService -> graphicValueService.value }.take(offset),
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatisticNumberConnection(
        @InputArgument("companyId") companyId: String,
    ): GraphicStatsByNumberConnection {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }
        return GraphicStatsByNumberConnection(
                values = statistic.statsNumberConnection!!.years.map { year ->
                    GraphicValueCompany.fromCompaniesStats(name = year.key.toString(), companiesStats = year.value, limit = 3)
                }
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
                values = statistic.statsNumberConnection!!.years[year.value]?.let {
                    listOf(GraphicValueCompany.fromCompaniesStats(name = year.value.toString(), companiesStats = it, limit = null))
                },
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatisticServicesProvided(
        @InputArgument("companyId") companyId: String,
        @InputArgument("offset") offset: Int,
    ): GraphicStatsByServiceProvider {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }

        return GraphicStatsByServiceProvider(
                values = statistic.statsServiceProvided!!.services.map {
                    GraphicValueService.fromEntity(it.value)
                }.sortedByDescending { graphicValueService -> graphicValueService.value }.take(offset),
        )
    }
}

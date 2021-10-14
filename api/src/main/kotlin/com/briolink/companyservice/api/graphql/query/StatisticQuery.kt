package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromCompaniesStats
import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.ServiceCompanyService
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
import java.util.UUID
import javax.persistence.EntityNotFoundException

@DgsComponent
class StatisticQuery(private val statisticReadRepository: StatisticReadRepository) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatistic(
        @InputArgument("companyId") companyId: String
    ): GraphicStatistics {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }

        val statsByCountry = GraphicStatsByCountry(
                values = statistic.statsByCountry!!.countries.map { country ->
                    GraphicValueCompany.fromCompaniesStats(name = country.key.toString(), companiesStats = country.value, limit = 3)
                },
        )
        val statsByIndustry = GraphicStatsByIndustry(
                values = statistic.statsByIndustry!!.industries.map { industry ->
                    GraphicValueCompany.fromCompaniesStats(name = industry.key.toString(), companiesStats = industry.value, limit = 3)
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
                },
        )
        return GraphicStatistics(
                statsByCountry = statsByCountry,
                statsByIndustry = statsByIndustry,
                statsByNumberConnection = statsByNumberConnection,
                statsByServiceProvided = statsByServiceProvided,
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatisticByCountry(
        @InputArgument("companyId") companyId: String
    ): GraphicStatsByCountry {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }
        return GraphicStatsByCountry(
                values = statistic.statsByCountry!!.countries.map { country ->
                    GraphicValueCompany.fromCompaniesStats(name = country.key.toString(), companiesStats = country.value, limit = 3)
                },
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatisticByIndustry(
        @InputArgument("companyId") companyId: String
    ): GraphicStatsByIndustry {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }
        return GraphicStatsByIndustry(
                values = statistic.statsByIndustry!!.industries.map { industry ->
                    GraphicValueCompany.fromCompaniesStats(name = industry.key.toString(), companiesStats = industry.value, limit = 3)
                },
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatisticNumberConnection(
        @InputArgument("companyId") companyId: String
    ): GraphicStatsByNumberConnection {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }
        return GraphicStatsByNumberConnection(
                values = statistic.statsNumberConnection!!.years.map { year ->
                    GraphicValueCompany.fromCompaniesStats(name = year.key.toString(), companiesStats = year.value, limit = 3)
                },
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getStatisticServicesProvided(
        @InputArgument("companyId") companyId: String
    ): GraphicStatsByServiceProvider {
        val statistic = statisticReadRepository.findByCompanyId(UUID.fromString(companyId))
                .orElseThrow { throw EntityNotFoundException("$companyId stats not found") }

        return GraphicStatsByServiceProvider(
                values = statistic.statsServiceProvided!!.services.map {
                    GraphicValueService.fromEntity(it.value)
                }.take(7),
        )
    }
}

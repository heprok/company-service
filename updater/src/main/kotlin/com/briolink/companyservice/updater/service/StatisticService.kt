package com.briolink.companyservice.updater.service

import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.StatisticReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.IndustryReadRepository
import com.briolink.companyservice.common.jpa.read.repository.StatisticReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class StatisticService(
    private val statisticReadRepository: StatisticReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyReadRepository: CompanyReadRepository,
) {
    fun refreshByCompany(companyId: UUID) {
        val statisticByCompany: StatisticReadEntity = statisticReadRepository.findByCompanyId(companyId)
                .orElse(StatisticReadEntity(companyId));
        val companyUpdated: CompanyReadEntity = companyReadRepository.findById(companyId)
                .orElseThrow { throw EntityNotFoundException("$companyId not found") }

        val statsNumberConnection = StatisticReadEntity.StatsNumberConnection()
        val statsByCountry = StatisticReadEntity.StatsByCountry()
        val statsByIndustry = StatisticReadEntity.StatsByIndustry()
        val statsServiceProvided = StatisticReadEntity.StatsServiceProvided()
//
//        val statsNumberConnection = statisticByCompany.statsNumberConnection ?: StatisticReadEntity.StatsNumberConnection()
//        val statsByCountry = statisticByCompany.statsByCountry ?: StatisticReadEntity.StatsByCountry()
//        val statsByIndustry =  statisticByCompany.statsByIndustry ?: StatisticReadEntity.StatsByIndustry()
//        val statsServiceProvided =  statisticByCompany.statsByCountry ?: StatisticReadEntity.StatsServiceProvided()
        val connectionsByCompany = connectionReadRepository.findByParticipantFromCompanyIdOrParticipantToCompanyId(companyId, companyId)
        val listCollaborator = mutableSetOf<UUID>()
        connectionsByCompany.forEach { connection ->
            val collaborator = if (connection.participantFromCompanyId != companyId) {
                StatisticReadEntity.Company(
                        id = connection.data.participantToCompany.id,
                        slug = connection.data.participantToCompany.slug,
                        name = connection.data.participantToCompany.name,
                        logo = connection.data.participantToCompany.logo,
                )
            } else {
                StatisticReadEntity.Company(
                        id = connection.data.participantFromCompany.id,
                        slug = connection.data.participantFromCompany.slug,
                        name = connection.data.participantFromCompany.name,
                        logo = connection.data.participantFromCompany.logo,
                )
            }
            listCollaborator.add(collaborator.id)
            statsNumberConnection.years[connection.created.year] = statsNumberConnection.years.getOrDefault(
                    connection.created.year,
                    StatisticReadEntity.CompaniesStats(
                            listCompanies = mutableSetOf(),
                            totalCount = mutableMapOf(),
                    ),
            ).apply {
                this.totalCount[collaborator.id] = this.totalCount.getOrDefault(collaborator.id, 0) + 1
                this.listCompanies.add(collaborator)
            }

            //TODO add industryString connection, country if null?
            val countyCollaborator: String
            val industryName: String
            companyReadRepository.findById(collaborator.id)
                    .orElseThrow { throw EntityNotFoundException(collaborator.id.toString() + " not found") }.data.also { data ->
                        countyCollaborator = data.location!!.split(",", ignoreCase = true, limit = 3)[1].trimStart()
                        industryName = data.industry!!.name
                    }
            statsByIndustry.industries[industryName] = statsByIndustry.industries.getOrDefault(
                    industryName,
                    StatisticReadEntity.CompaniesStats(
                            listCompanies = mutableSetOf(),
                            totalCount = mutableMapOf(),
                    ),
            ).apply {
                this.totalCount[collaborator.id] = this.totalCount.getOrDefault(collaborator.id, 0) + 1
                this.listCompanies.add(collaborator)
            }

            statsByCountry.countries[countyCollaborator] = statsByCountry.countries.getOrDefault(
                    countyCollaborator,
                    StatisticReadEntity.CompaniesStats(
                            listCompanies = mutableSetOf(),
                            totalCount = mutableMapOf(),
                    ),
            ).apply {
                this.totalCount[collaborator.id] = this.totalCount.getOrDefault(collaborator.id, 0) + 1
                this.listCompanies.add(collaborator)
            }

            if (connection.participantFromCompanyId == companyId) {
                connection.data.services.forEach {
                    statsServiceProvided.services[it.id] = statsServiceProvided.services.getOrDefault(
                            it.id,
                            StatisticReadEntity.ServiceStats(
                                    service = StatisticReadEntity.Service(
                                            id = it.id,
                                            name = it.name,
                                            slug = it.slug,
                                    ),
                                    totalCount = 0,
                            ),
                    ).apply {
                        this.totalCount = this.totalCount + 1
                    }
                }
            }
        }

        companyUpdated.data.statistic.serviceProvidedCount = statsServiceProvided.services.values.sumOf {
            it.totalCount
        }
        companyUpdated.data.statistic.collaboratingCompanyCount = listCollaborator.count()
        companyUpdated.data.statistic.totalConnectionCount = connectionsByCompany.count()

        statisticByCompany.statsNumberConnection = statsNumberConnection
        statisticByCompany.statsByIndustry = statsByIndustry
        statisticByCompany.statsByCountry = statsByCountry
        statisticByCompany.statsServiceProvided = statsServiceProvided
        statisticReadRepository.save(statisticByCompany)
        companyReadRepository.save(companyUpdated)
    }


}


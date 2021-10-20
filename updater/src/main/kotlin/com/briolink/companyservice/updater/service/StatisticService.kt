package com.briolink.companyservice.updater.service

import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.entity.StatisticReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
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
        val statisticByCompany = StatisticReadEntity(companyId)
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
        val connectionsByCompany = connectionReadRepository.findBySellerIdOrBuyerId(companyId, companyId)
        val listCollaborator = mutableSetOf<UUID>()
        connectionsByCompany.forEach { connection ->
            val collaborator = if (connection.sellerId != companyId) {
                StatisticReadEntity.Company(
                        id = connection.data.buyerCompany.id,
                        slug = connection.data.buyerCompany.slug,
                        name = connection.data.buyerCompany.name,
                        logo = connection.data.buyerCompany.logo,
                        role = StatisticReadEntity.Role(
                                name = connection.data.buyerCompany.role.name,
                                id = connection.data.buyerCompany.id,
                                type = ConnectionRoleReadEntity.RoleType.valueOf(connection.data.buyerCompany.role.type.ordinal.toString()),
                        ),
                )
            } else {
                StatisticReadEntity.Company(
                        id = connection.data.sellerCompany.id,
                        slug = connection.data.sellerCompany.slug,
                        name = connection.data.sellerCompany.name,
                        logo = connection.data.sellerCompany.logo,
                        role = StatisticReadEntity.Role(
                                name = connection.data.sellerCompany.role.name,
                                id = connection.data.sellerCompany.id,
                                type = ConnectionRoleReadEntity.RoleType.valueOf(connection.data.sellerCompany.role.type.ordinal.toString()),
                        ),
                )
            }.apply {
                val companyRead = companyReadRepository.findById(id).orElseThrow { throw EntityNotFoundException("$id company not found") }
                industry = companyRead.data.industry?.name
                location = companyRead.data.location
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

            if (connection.sellerId == companyId) {
                connection.data.services.forEach {
                    statsServiceProvided.services[it.id!!] = statsServiceProvided.services.getOrDefault(
                            it.id,
                            StatisticReadEntity.ServiceStats(
                                    service = StatisticReadEntity.Service(
                                            id = it.id!!,
                                            name = it.name!!,
                                            slug = it.slug!!,
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


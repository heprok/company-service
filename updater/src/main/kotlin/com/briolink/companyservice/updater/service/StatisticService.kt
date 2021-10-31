package com.briolink.companyservice.updater.service

import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.entity.StatisticReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.connection.ConnectionReadRepository
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
        val connectionsByCompany = connectionReadRepository.findBySellerIdOrBuyerIdAndVerificationStage(
                companyId,
                companyId,
                ConnectionReadEntity.ConnectionStatus.Verified,
        )
        val listCollaborator = mutableSetOf<UUID>()
        connectionsByCompany.forEach { connection ->
            val collaborator = if (connection.sellerId == companyId) {
                StatisticReadEntity.Company(
                        id = connection.data.buyerCompany.id,
                        slug = connection.data.buyerCompany.slug,
                        name = connection.data.buyerCompany.name,
                        logo = connection.data.buyerCompany.logo,
                        role = mutableSetOf(
                                StatisticReadEntity.Role(
                                        name = connection.data.buyerCompany.role.name,
                                        id = connection.data.buyerCompany.role.id,
                                        type = ConnectionRoleReadEntity.RoleType.values()[connection.data.buyerCompany.role.type.ordinal],
                                ),
                        ),
                )
            } else {
                StatisticReadEntity.Company(
                        id = connection.data.sellerCompany.id,
                        slug = connection.data.sellerCompany.slug,
                        name = connection.data.sellerCompany.name,
                        logo = connection.data.sellerCompany.logo,
                        role = mutableSetOf(
                                StatisticReadEntity.Role(
                                        name = connection.data.sellerCompany.role.name,
                                        id = connection.data.sellerCompany.role.id,
                                        type = ConnectionRoleReadEntity.RoleType.values()[connection.data.sellerCompany.role.type.ordinal],
                                ),
                        ),
                )
            }.apply {
                val companyRead = companyReadRepository.findById(id).orElseThrow { throw EntityNotFoundException("$id company not found") }
                industry = companyRead.data.industry?.name
                location = companyRead.data.location
                countService = connection.data.services.count()
            }
            listCollaborator.add(collaborator.id)
            statsNumberConnection.companiesStatsByYear[connection.created.year] = statsNumberConnection.companiesStatsByYear.getOrDefault(
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
            val countyCollaborator = collaborator.location?.let {
                it.split(",", ignoreCase = true, limit = 3)[1].trimStart()
            }
            val industryName = collaborator.industry
            statsByIndustry.companiesStatsByIndustry[industryName ?: "other"] = statsByIndustry.companiesStatsByIndustry.getOrDefault(
                    industryName ?: "other",
                    StatisticReadEntity.CompaniesStats(
                            listCompanies = mutableSetOf(),
                            totalCount = mutableMapOf(),
                    ),
            ).apply {
                if (this.listCompanies.find { company -> company.id == collaborator.id } == null) {
                    this.listCompanies.add(collaborator)
                } else {
                    this.listCompanies.find { company -> company.id == collaborator.id }?.role?.addAll(collaborator.role)
                }
                this.totalCount[collaborator.id] = this.totalCount.getOrDefault(collaborator.id, 0) + 1
            }
            statsByIndustry.companiesStatsByIndustry.forEach {
                statsByIndustry.totalCountByIndustry[it.key] = it.value.listCompanies.count()
            }

            statsByCountry.companiesStatsByCountry[countyCollaborator ?: "other"] = statsByCountry.companiesStatsByCountry.getOrDefault(
                    countyCollaborator ?: "other",
                    StatisticReadEntity.CompaniesStats(
                            listCompanies = mutableSetOf(),
                            totalCount = mutableMapOf(),
                    ),
            ).apply {
                if (this.listCompanies.find { company -> company.id == collaborator.id } == null) {
                    this.listCompanies.add(collaborator)
                } else {
                    this.listCompanies.find { company -> company.id == collaborator.id }?.role?.addAll(collaborator.role)
                }
                this.totalCount[collaborator.id] = this.totalCount.getOrDefault(collaborator.id, 0) + 1
            }
            statsByCountry.companiesStatsByCountry.forEach {
                statsByCountry.totalCountByCountry[it.key] = it.value.listCompanies.count()
            }
            if (connection.sellerId == companyId) {
//                connection.data.services.forEach {
//                    statsServiceProvided.services[it.id!!] = statsServiceProvided.services.getOrDefault(
//                            it.id,
//                            StatisticReadEntity.ServiceStats(
//                                    service = StatisticReadEntity.Service(
//                                            id = it.id!!,
//                                            name = it.name!!,
//                                            slug = it.slug!!,
//                                    ),
//                                    totalCount = 0,
//                            ),
//                    ).apply {
//                        this.totalCount = this.totalCount + 1
//                    }
//                }
                connection.data.services.forEach {
                    statsServiceProvided.companiesStatsByService[it.name!!] = statsServiceProvided.companiesStatsByService.getOrDefault(
                            it.name!!,
                            StatisticReadEntity.CompaniesStats(
                                    listCompanies = mutableSetOf(),
                                    totalCount = mutableMapOf(),
                            )
                    ).apply {
                        this.listCompanies.add(collaborator)
                        this.totalCount[collaborator.id] = this.totalCount.getOrDefault(collaborator.id, 0) + 1
                    }
                    statsServiceProvided.companiesStatsByService.forEach{
                        statsServiceProvided.totalCountByService[it.key] = it.value.totalCount.values.sum()
                    }
                }
            }
        }
        companyUpdated.data.statistic.serviceProvidedCount = statsServiceProvided.companiesStatsByService.values.sumOf{
            it.totalCount.values.sum()
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


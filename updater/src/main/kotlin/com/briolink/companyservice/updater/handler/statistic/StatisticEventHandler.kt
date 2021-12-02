package com.briolink.companyservice.updater.handler.statistic

import com.briolink.companyservice.common.event.v1_0.StatisticRefreshEvent
import com.briolink.companyservice.common.jpa.enumeration.CompanyRoleTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionServiceReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import java.util.stream.Collectors

@EventHandler("StatisticRefreshEvent", "1.0")
class StatisticEventHandler(
    private val companyReadRepository: CompanyReadRepository,
//    private val statisticHandlerService: StatisticHandlerService,
    private val connectionReadRepository: ConnectionReadRepository,
    private val connectionServiceReadRepository: ConnectionServiceReadRepository
) : IEventHandler<StatisticRefreshEvent> {
    override fun handle(event: StatisticRefreshEvent) {
//        val companiesUUID = event.data.companyId.let {
//            if (it == null) companyReadRepository.getAllCompanyUUID() else listOf(it)
//        }

        val companies = companyReadRepository.findAll()
        connectionReadRepository.findAll().forEach { connection ->

            val buyerCompany: CompanyReadEntity
            val sellerCompany: CompanyReadEntity
            val companyRole: String
            if (connection.participantFromRoleType == CompanyRoleTypeEnum.Buyer) {
                buyerCompany = companies.first { it.id == connection.participantFromCompanyId }!!
                sellerCompany = companies.first { it.id == connection.participantToCompanyId }
                companyRole = connection.participantFromRoleName
            } else {
                buyerCompany = companies.first { it.id == connection.participantToCompanyId }!!
                sellerCompany = companies.first { it.id == connection.participantFromCompanyId }!!
                companyRole = connection.participantFromRoleName
            }

            connection.data.services.forEach {
                val s = connectionServiceReadRepository.findById(it.id).orElse(
                    ConnectionServiceReadEntity().apply {
                        data = ConnectionServiceReadEntity.Data()
                    }
                )

                if (s.id == null) s.id = it.id
                s.companyId = sellerCompany.id
                s.serviceId = it.serviceId
                s.name = it.serviceName
                if (!s.collaboratingCompanyIds.contains(buyerCompany.id)) {
                    s.data.collaboratingCompanies[buyerCompany.id] = ConnectionServiceReadEntity.Company(
                        name = buyerCompany.name,
                        logo = buyerCompany.data.logo,
                        location = buyerCompany.data.location?.toString(),
                        industryName = buyerCompany.data.industry?.name,
                        slug = buyerCompany.slug
                    )
                    s.collaboratingCompanyIds.add(buyerCompany.id)
                }
                s.data.connectionsInfo.add(
                    ConnectionServiceReadEntity.ConnectionInfo(
                        companyId = buyerCompany.id,
                        roleName = companyRole,
                        periodUsedStart = it.startDate,
                        periodUsedEnd = it.endDate
                    )
                )

                connectionServiceReadRepository.save(s)
            }
        }

//        companiesUUID.forEach {
////            statisticHandlerService.refreshByCompanyId(it)
//        }
    }
}

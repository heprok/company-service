package com.briolink.companyservice.updater.handler.statistic

import com.briolink.companyservice.common.event.v1_0.StatisticRefreshEvent
import com.briolink.companyservice.common.jpa.enumeration.CompanyRoleTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionServiceReadRepository
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import org.springframework.context.ApplicationEventPublisher

@EventHandler("StatisticRefreshEvent", "1.0")
class StatisticEventHandler(
    private val companyReadRepository: CompanyReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val connectionServiceReadRepository: ConnectionServiceReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<StatisticRefreshEvent> {
    override fun handle(event: StatisticRefreshEvent) {
        val companies = companyReadRepository.findAll()
        event.data.companyId.let { if (it == null) companyReadRepository.getAllCompanyUUID() else listOf(it) }
            .forEach { companyId ->
                applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(companyId))
                connectionReadRepository.getByCompanyIdAndStatusAndNotHiddenOrNotDeleted(companyId)
                    .forEach { connection ->
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
                            val s =
                                connectionServiceReadRepository.findById(it.id).orElse(ConnectionServiceReadEntity())

                            if (s.id == null) s.id = it.id
                            s.collaboratingCompanyId = buyerCompany.id
                            s.companyId = sellerCompany.id
                            s.serviceId = it.serviceId
                            s.connectionId = connection.id
                            s.name = it.serviceName
                            s.status = connection.status
                            s.hidden = connection.hiddenCompanyIds.contains(sellerCompany.id)
                            s.data = ConnectionServiceReadEntity.Data(
                                company = ConnectionServiceReadEntity.Company(
                                    id = buyerCompany.id,
                                    name = buyerCompany.name,
                                    logo = buyerCompany.data.logo,
                                    location = buyerCompany.data.location?.toString(),
                                    industryName = buyerCompany.data.industry?.name,
                                    slug = buyerCompany.slug
                                ),
                                roleName = companyRole,
                                periodUsedStart = it.startDate,
                                periodUsedEnd = it.endDate
                            )
                            connectionServiceReadRepository.save(s)
                        }
                    }
            }
    }
}

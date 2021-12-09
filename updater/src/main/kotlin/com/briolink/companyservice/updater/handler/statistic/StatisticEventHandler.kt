package com.briolink.companyservice.updater.handler.statistic

import com.briolink.companyservice.common.event.v1_0.StatisticRefreshEvent
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import org.springframework.context.ApplicationEventPublisher

@EventHandler("StatisticRefreshEvent", "1.0")
class StatisticEventHandler(
    private val companyReadRepository: CompanyReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<StatisticRefreshEvent> {
    override fun handle(event: StatisticRefreshEvent) {
        val services = serviceReadRepository.findAllAndNotHiddenAndNotDeleted()
        event.data.companyId.let { if (it == null) companyReadRepository.getAllCompanyUUID() else listOf(it) }
            .forEach { companyId ->
                applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(companyId, false))
                connectionReadRepository.findAll()
                    .forEach { connection ->
                        connection.data.services.forEach { serviceConnection ->
                            serviceConnection.slug =
                                services.find { it.id == serviceConnection.serviceId }?.data?.slug ?: "-1"
                        }
                        connectionReadRepository.save(connection)
                    }
            }
    }
}

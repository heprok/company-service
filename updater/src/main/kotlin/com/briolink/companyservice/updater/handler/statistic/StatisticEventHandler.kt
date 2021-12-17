package com.briolink.companyservice.updater.handler.statistic

import com.briolink.companyservice.common.event.v1_0.StatisticRefreshEvent
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import org.springframework.context.ApplicationEventPublisher

@EventHandler("StatisticRefreshEvent", "1.0")
class StatisticEventHandler(
    private val companyReadRepository: CompanyReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<StatisticRefreshEvent> {
    override fun handle(event: StatisticRefreshEvent) {
        event.data.companyId.let { if (it == null) companyReadRepository.getAllCompanyUUID() else listOf(it) }
            .forEach { companyId ->
                applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(companyId, false))
            }
    }
}

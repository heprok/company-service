package com.briolink.companyservice.updater.handler.statistic

import com.briolink.companyservice.common.event.v1_0.StatisticRefreshEvent
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler

@EventHandler("StatisticRefreshEvent", "1.0")
class StatisticEventHandler(
    private val companyReadRepository: CompanyReadRepository,
    private val statisticHandlerService: StatisticHandlerService,
) : IEventHandler<StatisticRefreshEvent> {
    override fun handle(event: StatisticRefreshEvent) {
        val companiesUUID = event.data.companyId.let {
            if (it == null) companyReadRepository.getAllCompanyUUID() else listOf(it)
        }
        companiesUUID.forEach {
            statisticHandlerService.refreshByCompanyId(it)
        }
    }
}

package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.event.v1_0.StatisticRefreshEvent
import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import com.briolink.companyservice.common.jpa.read.entity.StatisticReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.OccupationReadRepository
import com.briolink.companyservice.updater.service.StatisticService

@EventHandler("StatisticRefreshEvent", "1.0")
class StatisticEventHandler(
    private val occupationReadRepository: OccupationReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val statisticService: StatisticService,
) : IEventHandler<StatisticRefreshEvent> {
    override fun handle(event: StatisticRefreshEvent) {
        val eventData = event.data
        val companiesUUID = companyReadRepository.findAll().map {
            it.id
        }
        companiesUUID.forEach {
            statisticService.refreshByCompany(it)
        }
    }
}

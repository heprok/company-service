package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository

@EventHandler("CompanyCreatedEvent", "1.0")
class CompanyCreatedEventHandler(
    private val companyReadRepository: CompanyReadRepository
) : IEventHandler<CompanyCreatedEvent> {
    override fun handle(event: CompanyCreatedEvent) {
        val dataEvent = event.data
        val company = CompanyReadEntity(dataEvent.id)
        val data = CompanyReadEntity.Data(
                name = dataEvent.name,
                website = dataEvent.website
        )
        company.data = data
        company.slug = dataEvent.slug
        companyReadRepository.save(company)
    }
}

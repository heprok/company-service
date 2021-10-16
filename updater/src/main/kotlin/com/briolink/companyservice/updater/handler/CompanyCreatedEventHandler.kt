package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository

@EventHandler("CompanyCreatedEvent", "1.0")
class CompanyCreatedEventHandler(
    private val companyReadRepository: CompanyReadRepository,
) : IEventHandler<CompanyCreatedEvent> {
    override fun handle(event: CompanyCreatedEvent) {
        val company = event.data
            companyReadRepository.save(
                    CompanyReadEntity(
                            id = company.id!!,
                            slug = company.slug!!,
                    ).apply {
                        data = CompanyReadEntity.Data(
                                name = company.name,
                                website = company.website,
                        )
                    },
            )
    }
}

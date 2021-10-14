package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import com.briolink.companyservice.common.util.StringUtil

@EventHandler("CompanyCreatedEvent", "1.0")
class CompanyCreatedEventHandler(
    private val companyReadRepository: CompanyReadRepository,
    private val companyWriteRepository: CompanyWriteRepository,
) : IEventHandler<CompanyCreatedEvent> {
    override fun handle(event: CompanyCreatedEvent) {
        val dataEvent = event.data
        val company = CompanyWriteEntity(
                name = dataEvent.name,
                website = dataEvent.website,
        )

        companyWriteRepository.save(company).apply {
            companyReadRepository.save(
                    CompanyReadEntity(
                            id = id!!,
                            slug = slug,
                    ).apply {
                        data = CompanyReadEntity.Data(
                                name = name,
                                website = website,
                        )
                    },
            )
        }
    }
}

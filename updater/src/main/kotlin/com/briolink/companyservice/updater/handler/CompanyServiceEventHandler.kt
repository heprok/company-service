package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.updater.event.CompanyServiceCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.updater.event.CompanyServiceUpdatedEvent
import javax.persistence.EntityNotFoundException

@EventHandler("CompanyServiceCreatedEvent", "1.0")
class CompanyServiceCreatedEventHandler(
    private val companyServiceReadRepository: ServiceReadRepository
) : IEventHandler<CompanyServiceCreatedEvent> {
    override fun handle(event: CompanyServiceCreatedEvent) {
        val eventData = event.data
        ServiceReadEntity(
                id = eventData.id,
                companyId = eventData.companyId,
                name = eventData.name,
                price = eventData.price,

                data = ServiceReadEntity.Data(
                        slug = eventData.slug,
                        logo = eventData.logo,
                ),
        ).apply {
            companyServiceReadRepository.save(this)
        }
    }
}

@EventHandler("CompanyServiceUpdatedEvent", "1.0")
class CompanyServiceUpdatedEventHandler(
    private val companyServiceReadRepository: ServiceReadRepository
) : IEventHandler<CompanyServiceUpdatedEvent> {
    override fun handle(event: CompanyServiceUpdatedEvent) {
        val eventData = event.data
        companyServiceReadRepository.findById(eventData.id)
                .orElseThrow { throw EntityNotFoundException(eventData.id.toString() + " companyService not found") }
                .apply {
                    id = eventData.id
                    companyId = eventData.companyId
                    name = eventData.name
                    price = eventData.price
                    verifiedUses = eventData.verifiedUses
                    lastUsed = eventData.lastUsed
                    data = ServiceReadEntity.Data(
                            slug = eventData.slug,
                            logo = eventData.logo,
                    )
                    companyServiceReadRepository.save(this)
                }
    }
}

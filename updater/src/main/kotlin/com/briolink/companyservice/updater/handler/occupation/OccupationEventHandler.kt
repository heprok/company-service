package com.briolink.companyservice.updater.handler.occupation

import com.briolink.companyservice.common.event.v1_0.OccupationCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.OccupationReadRepository
import org.springframework.transaction.annotation.Transactional

@EventHandler("OccupationCreatedEvent", "1.0")
class OccupationEventHandler(
    private val occupationHandlerService: OccupationHandlerService
) : IEventHandler<OccupationCreatedEvent> {
    override fun handle(event: OccupationCreatedEvent) {
        occupationHandlerService.create(event.data)
    }
}

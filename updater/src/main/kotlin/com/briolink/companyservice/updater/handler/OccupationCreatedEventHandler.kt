package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.event.v1_0.OccupationCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.OccupationReadRepository

@EventHandler("OccupationCreatedEvent", "1.0")
class OccupationCreatedEventHandler(
    private val occupationReadRepository: OccupationReadRepository
) : IEventHandler<OccupationCreatedEvent> {
    override fun handle(event: OccupationCreatedEvent) {
        val eventData = event.data
        val occupation = OccupationReadEntity().apply {
            this.id = eventData.id
            this.name = eventData.name

        }
        occupationReadRepository.save(occupation)
    }
}

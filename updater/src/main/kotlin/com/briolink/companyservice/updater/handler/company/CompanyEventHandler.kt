package com.briolink.companyservice.updater.handler.company

import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.updater.handler.connection.ConnectionHandlerService
import com.briolink.companyservice.updater.handler.connection.ConnectionServiceHandlerService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers

@EventHandlers(
    EventHandler("CompanyCreatedEvent", "1.0"),
    EventHandler("CompanyUpdatedEvent", "1.0"),
)
class CompanyEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val connectionHandlerService: ConnectionHandlerService,
    private val connectionServiceHandlerService: ConnectionServiceHandlerService
) : IEventHandler<CompanyCreatedEvent> {
    override fun handle(event: CompanyCreatedEvent) {
        companyHandlerService.createOrUpdate(event.data).let {
            if (event.name == "CompanyUpdatedEvent") {
                connectionHandlerService.updateCompany(it)
                connectionServiceHandlerService.updateCompany(it)
            }
        }
    }
}

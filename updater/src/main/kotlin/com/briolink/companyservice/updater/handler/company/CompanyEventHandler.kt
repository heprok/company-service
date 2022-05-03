package com.briolink.companyservice.updater.handler.company

import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.event.v1_0.CompanySyncEvent
import com.briolink.companyservice.updater.handler.project.ConnectionServiceHandlerService
import com.briolink.companyservice.updater.handler.project.ProjectHandlerService
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum

@EventHandlers(
    EventHandler("CompanyCreatedEvent", "1.0"),
    EventHandler("CompanyUpdatedEvent", "1.0"),
)
class CompanyEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val projectHandlerService: ProjectHandlerService,
    private val connectionServiceHandlerService: ConnectionServiceHandlerService,
) : IEventHandler<CompanyCreatedEvent> {
    override fun handle(event: CompanyCreatedEvent) {
        val updatedCompany = companyHandlerService.findById(event.data.id)
        val prevCountryId = updatedCompany?.data?.location?.country?.id
        val prevIndustryId = updatedCompany?.data?.industry?.id
        companyHandlerService.createOrUpdate(updatedCompany, event.data).let {
            if (event.name == "CompanyUpdatedEvent") {
                connectionServiceHandlerService.updateCompany(it)
                if (it.data.industry?.id != prevIndustryId || it.data.location?.country?.id != prevCountryId) {
                    projectHandlerService.refreshStatistic(event.data.id, true)
                }
            }
        }
    }
}

@EventHandler("CompanySyncEvent", "1.0")
class CompanySyncEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val connectionServiceHandlerService: ConnectionServiceHandlerService,
    syncService: SyncService,
) : SyncEventHandler<CompanySyncEvent>(ObjectSyncEnum.Company, syncService) {
    override fun handle(event: CompanySyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            val company = companyHandlerService.findById(objectSync.id)
            companyHandlerService.createOrUpdate(company, objectSync).also {
                connectionServiceHandlerService.updateCompany(it)
            }
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

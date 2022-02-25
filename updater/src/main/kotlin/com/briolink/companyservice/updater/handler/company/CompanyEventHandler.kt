package com.briolink.companyservice.updater.handler.company

import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.event.v1_0.CompanySyncEvent
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.companyservice.updater.handler.connection.ConnectionHandlerService
import com.briolink.companyservice.updater.handler.connection.ConnectionServiceHandlerService
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError
import org.springframework.context.ApplicationEventPublisher

@EventHandlers(
    EventHandler("CompanyCreatedEvent", "1.0"),
    EventHandler("CompanyUpdatedEvent", "1.0"),
)
class CompanyEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val connectionHandlerService: ConnectionHandlerService,
    private val connectionServiceHandlerService: ConnectionServiceHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<CompanyCreatedEvent> {
    override fun handle(event: CompanyCreatedEvent) {
        val updatedCompany = companyHandlerService.findById(event.data.id)
        val prevCountryId = updatedCompany?.data?.location?.country?.id
        val prevIndustryId = updatedCompany?.data?.industry?.id
        companyHandlerService.createOrUpdate(updatedCompany, event.data).let {
            if (event.name == "CompanyUpdatedEvent") {
                connectionHandlerService.updateCompany(it)
                connectionServiceHandlerService.updateCompany(it)
                if (it.data.industry?.id != prevIndustryId || it.data.location?.country?.id != prevCountryId) {
                    applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(event.data.id, true))
                }
            }
        }
    }
}

@EventHandler("CompanySyncEvent", "1.0")
class CompanySyncEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val syncService: SyncService,
    private val connectionHandlerService: ConnectionHandlerService,
    private val connectionServiceHandlerService: ConnectionServiceHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<CompanySyncEvent> {
    override fun handle(event: CompanySyncEvent) {
        val syncData = event.data
        if (syncData.objectSync == null) {
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.Company)
            return
        }
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSyncForService(syncData.syncId, syncData.service)
        try {
            val company = companyHandlerService.findById(syncData.objectSync!!.id)
            companyHandlerService.createOrUpdate(company, syncData.objectSync!!).also {
//                connectionHandlerService.updateCompany(it)
//                connectionServiceHandlerService.updateCompany(it)
//                applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(syncData.objectSync!!.id, false))
            }
        } catch (ex: Exception) {
            syncService.sendSyncError(
                syncError = SyncError(
                    service = syncData.service,
                    updater = UpdaterEnum.Company,
                    syncId = syncData.syncId,
                    exception = ex,
                    indexObjectSync = syncData.indexObjectSync,
                ),
            )
        }
        if (syncData.indexObjectSync == syncData.totalObjectSync)
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.Company)
    }
}

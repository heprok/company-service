package com.briolink.companyservice.updater.handler.keyword

import com.briolink.companyservice.common.event.v1_0.KeywordCreatedEvent
import com.briolink.companyservice.common.event.v1_0.KeywordSyncEvent
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError

@EventHandler("KeywordCreatedEvent", "1.0")
class KeywordEventHandler(
    private val keywordHandlerService: KeywordHandlerService
) : IEventHandler<KeywordCreatedEvent> {
    override fun handle(event: KeywordCreatedEvent) {
        keywordHandlerService.createOrUpdate(null, event.data)
    }
}

@EventHandler("KeywordSyncEvent", "1.0")
class KeywordSyncEventHandler(
    private val keywordHandlerService: KeywordHandlerService,
    private val syncService: SyncService,
) : IEventHandler<KeywordSyncEvent> {
    override fun handle(event: KeywordSyncEvent) {
        val syncData = event.data
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSyncForService(syncData.syncId, syncData.service)
        try {
            val objectSync = syncData.objectSync
            val keyword = keywordHandlerService.findById(objectSync.id)
            keywordHandlerService.createOrUpdate(keyword, objectSync)
        } catch (ex: Exception) {
            syncService.sendSyncError(
                syncError = SyncError(
                    service = syncData.service,
                    updater = UpdaterEnum.Company,
                    syncId = syncData.syncId,
                    exception = ex,
                    indexObjectSync = syncData.indexObjectSync
                )
            )
        }
        if (syncData.indexObjectSync == syncData.totalObjectSync)
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.CompanyKeyword)
    }
}

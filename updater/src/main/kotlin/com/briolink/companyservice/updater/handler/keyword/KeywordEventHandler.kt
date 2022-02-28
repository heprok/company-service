package com.briolink.companyservice.updater.handler.keyword

import com.briolink.companyservice.common.event.v1_0.KeywordCreatedEvent
import com.briolink.companyservice.common.event.v1_0.KeywordSyncEvent
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum

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
    syncService: SyncService,
) : SyncEventHandler<KeywordSyncEvent>(ObjectSyncEnum.CompanyKeyword, syncService) {
    override fun handle(event: KeywordSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            val keyword = keywordHandlerService.findById(objectSync.id)
            keywordHandlerService.createOrUpdate(keyword, objectSync)
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

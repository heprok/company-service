package com.briolink.companyservice.updater.handler.keyword

import com.briolink.companyservice.common.event.v1_0.KeywordCreatedEvent
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler

@EventHandler("KeywordCreatedEvent", "1.0")
class KeywordEventHandler(
    private val keywordHandlerService: KeywordHandlerService
) : IEventHandler<KeywordCreatedEvent> {
    override fun handle(event: KeywordCreatedEvent) {
        keywordHandlerService.create(event.data)
    }
}

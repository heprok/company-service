package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.event.v1_0.KeywordCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.KeywordReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.KeywordReadRepository

@EventHandler("KeywordCreatedEvent", "1.0")
class KeywordCreatedEventHandler(
    private val keywordReadRepository: KeywordReadRepository
) : IEventHandler<KeywordCreatedEvent> {
    override fun handle(event: KeywordCreatedEvent) {
        val eventData = event.data
        val keyword = KeywordReadEntity().apply {
            this.id = eventData.id
            this.name = eventData.name
        }
        keywordReadRepository.save(keyword)
    }
}

package com.briolink.companyservice.updater.handler.keyword

import com.briolink.companyservice.common.event.v1_0.KeywordCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.KeywordReadEntity
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.repository.KeywordReadRepository
import org.springframework.transaction.annotation.Transactional

@EventHandler("KeywordCreatedEvent", "1.0")
@Transactional
class KeywordEventHandler(
    private val keywordReadRepository: KeywordReadRepository
) : IEventHandler<KeywordCreatedEvent> {
    override fun handle(event: KeywordCreatedEvent) {
        val keyword = event.data
        KeywordReadEntity(
                id = keyword.id,
                name = keyword.name,
        ).apply {
            keywordReadRepository.saveAndFlush(this)
        }
    }
}

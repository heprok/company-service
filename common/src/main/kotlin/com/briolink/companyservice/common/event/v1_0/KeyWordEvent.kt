package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Keyword
import com.briolink.event.Event

data class KeyWordCreatedEvent(override val data: Keyword) : Event<Keyword>("1.0")
data class KeyWordUpdatedEvent(override val data: Keyword) : Event<Keyword>("1.0")
data class KeyWordDeletedEvent(override val data: Keyword) : Event<Keyword>("1.0")

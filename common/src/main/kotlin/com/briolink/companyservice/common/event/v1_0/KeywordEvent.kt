package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.companyservice.common.domain.v1_0.Keyword
import com.briolink.companyservice.common.domain.v1_0.KeywordSyncData
import com.briolink.event.Event

data class KeywordCreatedEvent(override val data: Keyword) : Event<Domain>("1.0")
data class KeywordSyncEvent(override val data: KeywordSyncData) : Event<Domain>("1.0")

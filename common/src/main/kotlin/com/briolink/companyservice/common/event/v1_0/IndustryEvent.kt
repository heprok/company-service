package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.lib.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent

data class IndustryCreatedEvent(override val data: Industry) : Event<Domain>("1.0")
data class IndustrySyncEvent(override val data: SyncData<Industry>) : SyncEvent<Industry>("1.0")

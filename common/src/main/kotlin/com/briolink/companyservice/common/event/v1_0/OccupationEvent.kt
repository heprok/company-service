package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.companyservice.common.domain.v1_0.Occupation
import com.briolink.lib.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent

data class OccupationCreatedEvent(override val data: Occupation) : Event<Domain>("1.0")
data class OccupationSyncEvent(override val data: SyncData<Occupation>) : SyncEvent<Occupation>("1.0")

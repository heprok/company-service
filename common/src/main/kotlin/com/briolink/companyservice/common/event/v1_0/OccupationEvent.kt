package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.companyservice.common.domain.v1_0.Occupation
import com.briolink.event.Event
import com.briolink.lib.sync.ISyncEvent
import com.briolink.lib.sync.enumeration.ServiceEnum

data class OccupationCreatedEvent(override val data: Occupation) : Event<Domain>("1.0")
data class OccupationSyncEvent(
    override val service: ServiceEnum,
    override val indexRow: Long,
    override val totalElements: Long,
    override val syncId: Int,
    override val isLastData: Boolean = false,
    override val data: Occupation
) : Event<Domain>("1.0"), ISyncEvent

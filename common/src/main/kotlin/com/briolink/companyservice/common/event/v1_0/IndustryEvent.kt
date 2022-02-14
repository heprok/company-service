package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.event.Event
import com.briolink.lib.sync.ISyncEvent
import com.briolink.lib.sync.enumeration.ServiceEnum

data class IndustryCreatedEvent(override val data: Industry) : Event<Domain>("1.0")
data class IndustrySyncEvent(
    override val service: ServiceEnum,
    override val indexRow: Long,
    override val totalElements: Long,
    override val syncId: Int,
    override val isLastData: Boolean = false,
    override val data: Industry
) : Event<Domain>("1.0"), ISyncEvent

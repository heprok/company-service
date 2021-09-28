package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.event.Event

data class IndustryCreatedEvent(override val data: Industry) : Event<Industry>("1.0")
data class IndustryUpdatedEvent(override val data: Industry) : Event<Industry>("1.0")
data class IndustryDeletedEvent(override val data: Industry) : Event<Industry>("1.0")

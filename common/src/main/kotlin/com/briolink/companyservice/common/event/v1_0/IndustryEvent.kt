package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.event.Event

data class IndustryCreatedEvent(override val data: Industry) : Event<Domain>("1.0")

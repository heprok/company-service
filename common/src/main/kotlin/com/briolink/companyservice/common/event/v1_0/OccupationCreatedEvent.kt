package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.companyservice.common.domain.v1_0.Occupation
import com.briolink.event.Event

data class OccupationCreatedEvent(override val data: Occupation) : Event<Domain>("1.0")

package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.event.Event

data class CompanyCreatedEvent(override val data: Company) : Event<Domain>("1.0")
data class CompanyUpdatedEvent(override val data: Company) : Event<Domain>("1.0")
//data class CompanyDeletedEvent(override val data: Company) : Event<Domain>("1.0")

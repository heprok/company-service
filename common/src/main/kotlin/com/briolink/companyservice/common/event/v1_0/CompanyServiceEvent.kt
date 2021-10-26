package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.domain.v1_0.CompanyService
import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.event.Event

data class CompanyServiceCreatedEvent(override val data: CompanyService) : Event<Domain>("1.0")
data class CompanyServiceUpdatedEvent(override val data: CompanyService) : Event<Domain>("1.0")
//data class CompanyDeletedEvent(override val data: Company) : Event<Domain>("1.0")

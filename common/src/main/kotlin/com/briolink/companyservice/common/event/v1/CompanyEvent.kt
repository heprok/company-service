package com.briolink.companyservice.common.event.v1

data class CompanyCreatedEvent(override val data: com.briolink.companyservice.common.domain.v1.Company) : com.briolink.companyservice.common.event.v1.Event<com.briolink.companyservice.common.domain.v1.Company>()
data class CompanyUpdatedEvent(override val data: com.briolink.companyservice.common.domain.v1.Company) : com.briolink.companyservice.common.event.v1.Event<com.briolink.companyservice.common.domain.v1.Company>()
data class CompanyDeletedEvent(override val data: com.briolink.companyservice.common.domain.v1.Company) : com.briolink.companyservice.common.event.v1.Event<com.briolink.companyservice.common.domain.v1.Company>()

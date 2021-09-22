package com.briolink.companyservice.common.event.v1

data class IndustryCreatedEvent(override val data: com.briolink.companyservice.common.domain.v1.Industry) : com.briolink.companyservice.common.event.v1.Event<com.briolink.companyservice.common.domain.v1.Industry>()
data class IndustryUpdatedEvent(override val data: com.briolink.companyservice.common.domain.v1.Industry) : com.briolink.companyservice.common.event.v1.Event<com.briolink.companyservice.common.domain.v1.Industry>()
data class IndustryDeletedEvent(override val data: com.briolink.companyservice.common.domain.v1.Industry) : com.briolink.companyservice.common.event.v1.Event<com.briolink.companyservice.common.domain.v1.Industry>()

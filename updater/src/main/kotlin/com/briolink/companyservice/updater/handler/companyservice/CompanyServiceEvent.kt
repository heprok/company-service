package com.briolink.companyservice.updater.handler.companyservice

import com.briolink.event.Event
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.UUID

data class CompanyServiceCreatedEvent(override val data: CompanyService) : Event<CompanyService>("1.0")
data class CompanyServiceUpdatedEvent(override val data: CompanyService) : Event<CompanyService>("1.0")
data class CompanyServiceDeletedEvent(override val data: CompanyServiceDeletedData) : Event<CompanyServiceDeletedData>("1.0")
data class CompanyServiceHideEvent(override val data: CompanyServiceHideData) : Event<CompanyServiceHideData>("1.0")

data class CompanyService(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val name: String,
    @JsonProperty
    val slug: String,
    @JsonProperty
    val price: Double? = null,
    @JsonProperty
    val logo: URL? = null
)

data class CompanyServiceDeletedData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val companyId: UUID,
)

data class CompanyServiceHideData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val hidden: Boolean,
)

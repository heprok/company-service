package com.briolink.companyservice.updater.handler.companyservice

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.UUID

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
    @JsonProperty
    val affectedConnections: ArrayList<UUID>
)

data class CompanyServiceHideData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val affectedConnections: ArrayList<UUID>
)

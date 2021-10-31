package com.briolink.companyservice.updater.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.time.LocalDate
import java.util.*

data class CompanyService(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("companyId")
    val companyId: UUID,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("slug")
    val slug: String,
    @JsonProperty("price")
    val price: Double? = null,
    @JsonProperty("logo")
    val logo: URL? = null,
    @JsonProperty("lastUsed")
    val lastUsed: LocalDate? = null,
    @JsonProperty("verifiedUses")
    val verifiedUses: Int = 0,
)

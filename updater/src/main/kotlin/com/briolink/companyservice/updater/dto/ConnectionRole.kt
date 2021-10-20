package com.briolink.companyservice.updater.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ConnectionRole(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("type")
    val type: RoleType,
)

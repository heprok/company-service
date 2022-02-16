package com.briolink.companyservice.common.domain.v1_0

import com.briolink.lib.sync.ISyncData
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Industry(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String,
) : Domain

data class IndustrySyncData(
    @JsonProperty
    override val indexObjectSync: Long,
    @JsonProperty
    override val totalObjectSync: Long,
    @JsonProperty
    override val objectSync: Industry,
    @JsonProperty
    override val service: ServiceEnum,
    @JsonProperty
    override val syncId: Int
) : ISyncData<Industry>, Domain

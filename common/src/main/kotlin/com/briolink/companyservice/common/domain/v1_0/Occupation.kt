package com.briolink.companyservice.common.domain.v1_0

import com.briolink.lib.sync.ISyncData
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Occupation(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String
) : Domain

data class OccupationSyncData(
    @JsonProperty
    override val indexObjectSync: Long,
    @JsonProperty
    override val totalObjectSync: Long,
    @JsonProperty
    override val objectSync: Occupation?,
    @JsonProperty
    override val service: ServiceEnum,
    @JsonProperty
    override val syncId: Int
) : ISyncData<Occupation>, Domain

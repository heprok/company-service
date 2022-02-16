package com.briolink.companyservice.common.domain.v1_0

import com.briolink.lib.sync.ISyncData
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Keyword(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String
) : Domain

data class KeywordSyncData(
    @JsonProperty
    override val indexObjectSync: Long,
    @JsonProperty
    override val totalObjectSync: Long,
    @JsonProperty
    override val objectSync: Keyword,
    @JsonProperty
    override val service: ServiceEnum,
    @JsonProperty
    override val syncId: Int
) : ISyncData<Keyword>, Domain

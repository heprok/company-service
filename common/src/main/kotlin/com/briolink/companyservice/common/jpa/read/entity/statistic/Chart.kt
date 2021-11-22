package com.briolink.companyservice.common.jpa.read.entity.statistic

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class Chart(
    @JsonProperty
    val tabs: MutableList<ChartTabItem> = mutableListOf(),
    @JsonProperty
    val items: MutableList<ChartItem> = mutableListOf()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartItem(
    @JsonProperty
    val key: String,
    @JsonProperty
    val name: String,
    @JsonProperty
    var value: Int,
    @JsonProperty
    var companyIds: MutableSet<UUID>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartTabItem(
    @JsonProperty
    val id: String,
    @JsonProperty
    val name: String,
    @JsonProperty
    var total: Int
)

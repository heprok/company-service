package com.briolink.companyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonProperty

data class Statistic(
    @JsonProperty("serviceProvidedCount")
    val serviceProvidedCount: Int = 0,
    @JsonProperty("collaboratingCompanyCount")
    val collaboratingCompanyCount: Int = 0,
    @JsonProperty("collaboratingPeopleCount")
    val collaboratingPeopleCount: Int = 0,
    @JsonProperty("totalConnectionCount")
    val totalConnectionCount: Int = 0
) : Domain

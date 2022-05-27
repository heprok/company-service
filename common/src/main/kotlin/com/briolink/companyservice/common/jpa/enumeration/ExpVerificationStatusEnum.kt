package com.briolink.companyservice.common.jpa.enumeration

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

enum class ExpVerificationStatusEnum(@JsonValue val value: Int) {
    @JsonProperty("1")
    NotConfirmed(1),

    @JsonProperty("2")
    Pending(2),

    @JsonProperty("3")
    Confirmed(3),

    @JsonProperty("4")
    Rejected(4);

    companion object {
        private val map = values().associateBy(ExpVerificationStatusEnum::value)
        fun ofValue(type: Int): ExpVerificationStatusEnum = map[type]!!
    }
}

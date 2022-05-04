package com.briolink.companyservice.common.jpa.enumeration

import com.fasterxml.jackson.annotation.JsonProperty

enum class ConnectionObjectTypeEnum(val value: Int) {
    @JsonProperty("1")
    User(1),

    @JsonProperty("2")
    Company(2);

    companion object {
        private val map = values().associateBy(ConnectionObjectTypeEnum::value)
        fun ofValue(value: Int): ConnectionObjectTypeEnum = map[value]!!
    }
}

package com.briolink.companyservice.common.jpa.enumration

import com.fasterxml.jackson.annotation.JsonValue

enum class LocationTypeEnum(@JsonValue val value: Int) {
    Country(0),
    State(1),
    City(2);

    companion object {
        private val map = values().associateBy(LocationTypeEnum::value)
        fun fromInt(type: Int): LocationTypeEnum = map[type]!!
    }
}

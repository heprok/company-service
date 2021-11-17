package com.briolink.companyservice.common.jpa.enumration

import com.fasterxml.jackson.annotation.JsonValue

enum class AccessObjectTypeEnum(@JsonValue val value: Int) {
    Company(1),
    CompanyService(2),
    Connection(3);

    companion object {
        private val map = values().associateBy(AccessObjectTypeEnum::value)
        fun fromInt(type: Int): AccessObjectTypeEnum = map[type]!!
    }
}

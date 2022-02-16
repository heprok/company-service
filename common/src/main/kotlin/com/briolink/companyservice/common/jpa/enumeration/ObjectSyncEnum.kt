package com.briolink.companyservice.common.jpa.enumeration

import com.fasterxml.jackson.annotation.JsonValue

enum class ObjectSyncEnum(@JsonValue val value: Int) {
    Company(0),
    CompanyIndustry(1),
    CompanyOccupation(2),
    User(3),
    UserJobPosition(4),
    Connection(5),
    CompanyService(6),
    CompanyKeyword(7);

    companion object {
        private val map = values().associateBy(ObjectSyncEnum::value)
        fun fromInt(type: Int): ObjectSyncEnum = map[type]!!
    }
}

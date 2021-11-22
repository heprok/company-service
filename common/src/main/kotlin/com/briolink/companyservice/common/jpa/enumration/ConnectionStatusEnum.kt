package com.briolink.companyservice.common.jpa.enumration

import com.fasterxml.jackson.annotation.JsonValue

enum class ConnectionStatusEnum(@JsonValue val value: Int) {
    Draft(1),
    Pending(2),
    InProgress(3),
    Verified(4),
    Rejected(5);

    companion object {
        private val map = values().associateBy(ConnectionStatusEnum::value)
        fun fromInt(type: Int): ConnectionStatusEnum = map[type]!!
    }
}

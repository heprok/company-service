package com.briolink.companyservice.common.jpa.enumeration

import com.fasterxml.jackson.annotation.JsonValue

enum class UserJobPositionVerifyStatusEnum(@JsonValue val value: Int) {
    Pending(1),
    Verified(2),
    Rejected(3);

    companion object {
        private val map = values().associateBy(UserJobPositionVerifyStatusEnum::value)
        fun fromInt(type: Int): UserJobPositionVerifyStatusEnum = map[type]!!
    }
}

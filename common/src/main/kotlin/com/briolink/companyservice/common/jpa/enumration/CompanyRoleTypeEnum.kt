package com.briolink.companyservice.common.jpa.enumration

import com.fasterxml.jackson.annotation.JsonValue

enum class CompanyRoleTypeEnum(@JsonValue val value: Int) {
    Buyer(0),
    Seller(1);

    companion object {
        private val map = values().associateBy(CompanyRoleTypeEnum::value)
        fun fromInt(type: Int): CompanyRoleTypeEnum = map[type]!!
    }
}

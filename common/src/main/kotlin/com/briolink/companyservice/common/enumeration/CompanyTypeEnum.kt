package com.briolink.companyservice.common.enumeration

import com.briolink.lib.common.jpa.type.PersistentEnum
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

enum class CompanyTypeEnum(@JsonValue override val value: String) : PersistentEnum {
    @JsonProperty("1")
    Startup("1"),

    @JsonProperty("2")
    Investor("2"),

    @JsonProperty("3")
    ServicesProvider("3");

    companion object {
        val map = values().associateBy(CompanyTypeEnum::value)
        fun fromValue(value: String): CompanyTypeEnum = map[value] ?: throw IllegalArgumentException("Unknown value $value")
    }
}

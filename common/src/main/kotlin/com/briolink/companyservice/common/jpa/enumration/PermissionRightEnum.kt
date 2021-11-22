package com.briolink.companyservice.common.jpa.enumration

import com.fasterxml.jackson.annotation.JsonValue

enum class PermissionRightEnum(@JsonValue val value: Int) {
    SetOwner(0),
    SetAdmin(1),
    SetSuperuser(2),
    EditCompanyProfile(3),
    VerifyCollegue(4),
    ConnectionCrud(5),
    ServiceCrud(6);

    companion object {
        private val map = values().associateBy(PermissionRightEnum::value)
        fun fromInt(type: Int): PermissionRightEnum = map[type]!!
    }
}

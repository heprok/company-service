package com.briolink.companyservice.common.jpa.enumration

import com.fasterxml.jackson.annotation.JsonValue

enum class UserPermissionRoleTypeEnum(@JsonValue val value: Int) {
    Employee(0),
    Owner(1);

    companion object {
        private val map = values().associateBy(UserPermissionRoleTypeEnum::value)
        fun fromInt(type: Int): UserPermissionRoleTypeEnum = map[type]!!
    }
}

package com.briolink.companyservice.common.jpa.enumeration

import com.fasterxml.jackson.annotation.JsonValue

enum class ProjectStageEnum(@JsonValue val value: Int) {
    Draft(1),
    Pending(2),
    InProgress(3),
    Verified(4),
    Rejected(5);

    companion object {
        private val map = values().associateBy(ProjectStageEnum::value)
        fun fromInt(type: Int): ProjectStageEnum = map[type]!!
    }
}

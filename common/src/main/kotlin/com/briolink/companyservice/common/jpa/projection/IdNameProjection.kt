package com.briolink.companyservice.common.jpa.projection

import java.util.*

interface IdNameProjection {
    val id: UUID
    val name: String
}

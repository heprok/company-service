package com.briolink.companyservice.common.jpa.projection

import java.util.UUID

interface IdNameProjection {
    val id: UUID
    val name: String
}

package com.briolink.companyservice.common.jpa.projection

import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import java.util.*

interface CollaboratorRoleProjection {
    val id: UUID
    val name: String
    val type: ConnectionRoleReadEntity.RoleType?
}

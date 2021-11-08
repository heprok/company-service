package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.companyservice.common.jpa.write.entity.BaseWriteEntity
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "user_permission_role", schema = "read")
@Entity
class UserPermissionRoleReadEntity(
    @Column(name = "access_object_uuid", nullable = false)
    @Type(type = "pg-uuid")
    var accessObjectUuid: UUID,

    @Column(name = "access_object_type", nullable = false)
    var accessObjectType: Int = 1,

    @Column(name = "user_id", nullable = false)
    @Type(type = "pg-uuid")
    var userId: UUID,

    @Column(name = "role", nullable = false)
    var role: RoleType = RoleType.Employee

) : BaseWriteEntity() {
    enum class RoleType {
        Employee,
        Owner,
    }
}

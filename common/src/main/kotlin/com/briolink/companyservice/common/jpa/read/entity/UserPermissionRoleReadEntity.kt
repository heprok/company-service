package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.companyservice.common.jpa.write.entity.BaseWriteEntity
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "user_permission_role", catalog = "schema_read")
@Entity
class UserPermissionRoleReadEntity(
    @Column(name = "access_object_uuid", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var accessObjectUuid: UUID,

    @Column(name = "access_object_type", nullable = false, length = 36)
    var accessObjectType: Int = 1,

    @Column(name = "user_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var userId: UUID,

    @Column(name = "role", nullable = false)
    var role: RoleType = RoleType.Employee

) : BaseWriteEntity() {
    enum class RoleType {
        Employee,
        Owner,
    }
}

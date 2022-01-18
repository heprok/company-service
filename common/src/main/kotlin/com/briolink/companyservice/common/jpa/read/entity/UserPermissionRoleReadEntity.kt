package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.companyservice.common.jpa.write.entity.BaseWriteEntity
import com.briolink.permission.enumeration.AccessObjectTypeEnum
import com.briolink.permission.enumeration.PermissionRoleEnum
import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "user_permission_role", schema = "read")
@Entity
class UserPermissionRoleReadEntity(

    @Type(type = "pg-uuid")
    @Column(name = "access_object_uuid", nullable = false)
    var accessObjectUuid: UUID,

    @Type(type = "pg-uuid")
    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "access_object_type", nullable = false)
    private var _accessObjectType: Int = AccessObjectTypeEnum.Company.id,

    @Column(name = "role", nullable = false)
    private var _role: Int = PermissionRoleEnum.Employee.id

) : BaseWriteEntity() {

    var accessObjectType: AccessObjectTypeEnum
        get() = AccessObjectTypeEnum.fromId(_accessObjectType)
        set(value) {
            _accessObjectType = value.value
        }

    var role: UserPermissionRoleTypeEnum
        get() = UserPermissionRoleTypeEnum.fromInt(_role)
        set(value) {
            _role = value.value
        }
}

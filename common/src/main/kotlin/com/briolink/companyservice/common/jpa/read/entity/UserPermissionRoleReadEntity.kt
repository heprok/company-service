package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.briolink.lib.permission.enumeration.PermissionRoleEnum
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "user_permission_role", schema = "read")
@Entity
class UserPermissionRoleReadEntity(

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    val id: UUID,

    @Type(type = "pg-uuid")
    @Column(name = "access_object_uuid", nullable = false)
    var accessObjectUuid: UUID,

    @Type(type = "pg-uuid")
    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "access_object_type", nullable = false)
    private var _accessObjectType: Int = AccessObjectTypeEnum.Company.id,

    @Column(name = "role", nullable = false)
    private var _role: Int = PermissionRoleEnum.Employee.id,

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    var data: Data
) : BaseReadEntity() {

    data class Data(
        @JsonProperty
        var level: Int,
        @JsonProperty
        var enabledPermissionRights: List<PermissionRightEnum>
    )

    var accessObjectType: AccessObjectTypeEnum
        get() = AccessObjectTypeEnum.ofId(_accessObjectType)
        set(value) {
            _accessObjectType = value.id
        }

    var role: PermissionRoleEnum
        get() = PermissionRoleEnum.ofId(_role)
        set(value) {
            _role = value.id
        }
}

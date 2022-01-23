package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.companyservice.common.jpa.enumeration.UserJobPositionVerifyStatusEnum
import com.briolink.permission.enumeration.PermissionRightEnum
import com.briolink.permission.enumeration.PermissionRoleEnum
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.time.LocalDate
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "user_job_position", schema = "read")
@Entity
class UserJobPositionReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    val id: UUID,

    @Type(type = "pg-uuid")
    @Column(name = "company_id", nullable = false)
    var companyId: UUID,

    @Type(type = "pg-uuid")
    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "status", nullable = false)
    private var _status: Int = UserJobPositionVerifyStatusEnum.Pending.value,

    @Column(name = "end_date")
    var endDate: LocalDate? = null,

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    var data: Data
) : BaseReadEntity() {

    var status: UserJobPositionVerifyStatusEnum
        get() = UserJobPositionVerifyStatusEnum.fromInt(_status)
        set(value) {
            _status = value.value
        }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty
        var user: User,
        @JsonProperty
        var userPermission: UserPermission? = null,
        @JsonProperty
        var title: String,
        @JsonProperty
        var verifiedBy: UUID? = null,
        @JsonProperty
        var isCurrent: Boolean = false,
        @JsonProperty
        var startDate: LocalDate? = null,
        @JsonProperty
        var endDate: LocalDate? = null,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class User(
        @JsonProperty
        var firstName: String,
        @JsonProperty
        var slug: String,
        @JsonProperty
        var lastName: String,
        @JsonProperty
        var image: URL? = null,
    )

    data class UserPermission(
        @JsonProperty
        val role: PermissionRoleEnum,
        @JsonProperty
        val level: Int,
        @JsonProperty
        val rights: List<PermissionRightEnum>
    )
}

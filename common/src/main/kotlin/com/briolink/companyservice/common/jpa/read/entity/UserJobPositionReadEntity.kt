package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.companyservice.common.jpa.enumeration.ExpVerificationStatusEnum
import com.briolink.lib.permission.model.UserPermissionRights
import com.fasterxml.jackson.annotation.JsonProperty
import com.vladmihalcea.hibernate.type.range.Range
import org.hibernate.annotations.ColumnTransformer
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
    private var _status: Int = ExpVerificationStatusEnum.NotConfirmed.value,

) : BaseReadEntity() {
    @ColumnTransformer(write = "to_tsvector('simple', ?)")
    @Column(name = "user_full_name_tsv", nullable = false)
    lateinit var userFullNameTsv: String

    @Column(name = "user_full_name", nullable = false)
    lateinit var userFullName: String

    @Column(name = "job_title", nullable = false)
    lateinit var jobTitle: String

    @ColumnTransformer(write = "to_tsvector('simple', ?)")
    @Column(name = "job_title_tsv", nullable = false)
    lateinit var jobTitleTsv: String

    @Column(name = "dates", columnDefinition = "daterange", nullable = false)
    lateinit var dates: Range<LocalDate>

    @Type(type = "text-array")
    @Column(name = "rigths", columnDefinition = "text[]")
    var rights: Array<String>? = null

    @Column(name = "permission_level")
    var permissionLevel: Int? = null

    @Column(name = "is_current", nullable = false)
    var isCurrent: Boolean = false

    var status: ExpVerificationStatusEnum
        get() = ExpVerificationStatusEnum.ofValue(_status)
        set(value) {
            _status = value.value
        }

    @Column(name = "is_verify_by_company", nullable = false)
    var isVerifyByCompany: Boolean = false

    @Type(type = "pg-uuid")
    @Column(name = "verification_id")
    var verificationId: UUID? = null

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    lateinit var data: Data

    fun setFormerEmployee() {
        dates = Range.closed(dates.lower(), LocalDate.now())
    }

    fun removeRights() {
        permissionLevel = null
        data.userPermission = null
        rights = null
    }

    data class Data(
        @JsonProperty
        var user: User,
        @JsonProperty
        var userPermission: UserPermissionRights? = null,
        @JsonProperty
        var jobPosition: UserJobPosition,
        @JsonProperty
        var verifiedBy: UUID? = null,

    )

    data class UserJobPosition(
        @JsonProperty
        var id: UUID,
        @JsonProperty
        var title: String,
        @JsonProperty
        var startDate: LocalDate,
        @JsonProperty
        var endDate: LocalDate? = null,
    )

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
}

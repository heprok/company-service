package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.io.Serializable
import java.net.URL
import java.time.LocalDate
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Embeddable
class EmployeeReadEntityId(
    var companyId: UUID,
    var userId: UUID
) : Serializable

@Table(name = "employee", schema = "read")
@Entity
@IdClass(EmployeeReadEntityId::class)
class EmployeeReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "company_id", nullable = false)
    val companyId: UUID,

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Type(type = "uuid-array")
    @Column(name = "user_job_position_ids", columnDefinition = "uuid[]")
    var userJobPositionIds: MutableList<UUID> = mutableListOf(),

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    var data: Data
) : BaseReadEntity() {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    enum class VerifyStatus {
        Pending, Verified, Rejected
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty
        var user: User,
        @JsonProperty
        var userJobPositions: MutableMap<UUID, UserJobPosition> = mutableMapOf()
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class UserJobPosition(
        @JsonProperty
        var status: VerifyStatus = VerifyStatus.Verified,
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
}

package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonFormat
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
    var id: UUID,

    @Type(type = "pg-uuid")
    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Type(type = "pg-uuid")
    @Column(name = "company_id")
    var companyId: UUID? = null,
) : BaseReadEntity() {
    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    lateinit var data: Data

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    enum class VerifyStatus {
        Pending, Verified, Rejected
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("User")
        var user: User,
        @JsonProperty
        var status: VerifyStatus = VerifyStatus.Verified,
        @JsonProperty
        var verifiedBy: UUID? = null,
        @JsonProperty
        var title: String,
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
}

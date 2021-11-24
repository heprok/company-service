package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.hibernate.annotations.Type

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
        @JsonProperty("status")
        var status: VerifyStatus = VerifyStatus.Verified,
        @JsonProperty("verifiedBy")
        var verifiedBy: UUID? = null,
        @JsonProperty("jobPosition")
        var title: String? = null,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class User(
        @JsonProperty("firstName")
        var firstName: String,
        @JsonProperty("slug")
        var slug: String,
        @JsonProperty("lastName")
        var lastName: String,
        @JsonProperty("image")
        var image: URL? = null,
    )
}

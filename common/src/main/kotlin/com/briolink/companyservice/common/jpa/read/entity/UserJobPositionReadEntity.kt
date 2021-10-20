package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "user_job_position", catalog = "schema_read")
@Entity
class UserJobPositionReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    var id: UUID,

    @Type(type = "uuid-char")
    @Column(name = "user_id", nullable = false, length = 36)
    var userId: UUID,

    @Type(type = "uuid-char")
    @Column(name = "company_id", length = 36)
    var companyId: UUID? = null,
) : BaseReadEntity() {
    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    lateinit var data: Data

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    enum class VerifyStatus {
        Pending, Verified, Rejected
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("status")
        var user: User,
        @JsonProperty("status")
        var status: VerifyStatus = VerifyStatus.Pending,
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

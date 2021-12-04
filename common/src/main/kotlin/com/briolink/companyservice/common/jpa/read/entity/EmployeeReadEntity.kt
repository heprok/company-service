package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.io.Serializable
import java.net.URL
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

class EmployeePK(
    var companyId: UUID,
    var userId: UUID
) : Serializable

@Table(name = "employee", schema = "read")
@Entity
@IdClass(EmployeePK::class)
class EmployeeReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "company_id")
    var companyId: UUID,

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "user_id")
    var userId: UUID,

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "user_job_position_id")
    var userJobPositionId: UUID,

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    var data: Data
) : BaseReadEntity() {

    companion object {
        fun fromUserJobPosition(userJobPosition: UserJobPositionReadEntity) = EmployeeReadEntity(
            userId = userJobPosition.userId,
            companyId = userJobPosition.companyId,
            userJobPositionId = userJobPosition.id,
            data = Data(
                user = User(
                    firstName = userJobPosition.data.user.firstName,
                    slug = userJobPosition.data.user.slug,
                    lastName = userJobPosition.data.user.lastName,
                    image = userJobPosition.data.user.image
                ),
                jobPosition = userJobPosition.data.title
            )
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty
        var user: User,
        @JsonProperty
        var jobPosition: String,
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

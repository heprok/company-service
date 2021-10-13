package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.time.LocalDate
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(
        name = "connection",
        catalog = "schema_read"
)
@Entity
class ConnectionReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    val id: UUID
) : BaseReadEntity() {

    @Column(name = "participant_from_company_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var participantFromCompanyId: UUID? = null

    @Column(name = "participant_to_company_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var participantToCompanyId: UUID? = null

    @Column(name = "participant_from_role_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var participantFromRoleId: UUID? = null

    @Column(name = "participant_to_role_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var participantToRoleId: UUID? = null

    @Column(name = "service_ids", nullable = false)
    var serviceIds: String? = null

    @Column(name = "dates_start_collaboration", nullable = false)
    var datesStartCollaboration: String? = null

    @Column(name = "dates_end_collaboration", nullable = false)
    var datesEndCollaboration: String? = null

    @Column(name = "industry_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var industryId: UUID? = null

    @Column(name = "verification_stage", nullable = false)
    var verificationStage: Int = 0

    @Column(name = "created", nullable = false)
    lateinit var created: LocalDate

    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("id")
        var id: UUID,
    ) {
        @JsonProperty("participantToCompany")
        lateinit var participantToCompany: ParticipantCompany
        @JsonProperty("participantFromCompany")
        lateinit var participantFromCompany: ParticipantCompany
        @JsonProperty("services")
        lateinit var services: List<Service>
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ParticipantCompany(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("slug")
        var slug: String,
        @JsonProperty("logo")
        var logo: URL?,
        @JsonProperty("verifyUser")
        var verifyUser: VerifyUser,
    ) {
        @JsonProperty("role")
        lateinit var role: Role
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Role(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("name")
        var name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Service(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("slug")
        var slug: String,
        @JsonProperty("endDate")
        val endDate: LocalDate,
        @JsonProperty("startDate")
        val startDate: LocalDate,
        @JsonProperty("industry")
        val industry: Industry
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Industry(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("name")
        var name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class VerifyUser(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("firstName")
        var firstName: String,
        @JsonProperty("lastName")
        var lastName: String,
        @JsonProperty("image")
        var image: URL?,
        @JsonProperty("slug")
        var slug: String,
    )
}



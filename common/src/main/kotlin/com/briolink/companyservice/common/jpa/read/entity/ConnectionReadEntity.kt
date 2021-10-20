package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.time.LocalDate
import java.time.Year
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
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    enum class ConnectionStatus(val value: Int) {
        Draft(1),
        Pending(2),
        InProgress(3),
        Verified(4),
        Rejected(5)
    }

    @Column(name = "seller_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var sellerId: UUID? = null

    @Column(name = "buyer_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var buyerId: UUID? = null

    @Column(name = "seller_role_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var sellerRoleId: UUID? = null

    @Column(name = "buyer_role_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var buyerRoleId: UUID? = null

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
    var verificationStage: ConnectionStatus = ConnectionStatus.Pending


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
        @JsonProperty("buyerCompany")
        lateinit var buyerCompany: ParticipantCompany
        @JsonProperty("sellerCompany")
        lateinit var sellerCompany: ParticipantCompany
        @JsonProperty("services")
        lateinit var services: List<Service>
        @JsonProperty("industry")
        lateinit var industry: Industry
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
        @JsonProperty("role")
        var role: Role
    ) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Role(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("role")
        var type: ConnectionRoleReadEntity.RoleType,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Service(
        @JsonProperty("id")
        var id: UUID? = null,
        @JsonProperty("name")
        var name: String?,
        @JsonProperty("slug")
        var slug: String? = null,
        @JsonProperty("endDate")
        val endDate: Year?,
        @JsonProperty("startDate")
        val startDate: Year,
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



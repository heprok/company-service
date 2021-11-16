package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.time.Instant
import java.time.Year
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.PrePersist
import javax.persistence.Table

@Table(name = "connection", schema = "read")
@Entity
class ConnectionReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
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

    @Column(name = "seller_id", nullable = false)
    @Type(type = "pg-uuid")
    lateinit var sellerId: UUID

    @Column(name = "buyer_id", nullable = false)
    @Type(type = "pg-uuid")
    lateinit var buyerId: UUID

    @Column(name = "seller_role_id", nullable = false)
    @Type(type = "pg-uuid")
    lateinit var sellerRoleId: UUID

    @Column(name = "buyer_role_id", nullable = false)
    @Type(type = "pg-uuid")
    lateinit var buyerRoleId: UUID

    @Column(name = "seller_name", nullable = false, length = 255)
    var sellerName: String? = null

    @Column(name = "buyer_name", nullable = false, length = 255)
    var buyerName: String? = null

    @Column(name = "seller_role_name", nullable = false, length = 255)
    var sellerRoleName: String? = null

    @Column(name = "buyer_role_name", nullable = false, length = 255)
    var buyerRoleName: String? = null

    @Column(name = "location", nullable = false, length = 255)
    var location: String? = null


    @Type(type = "list-array")
    @Column(
            name = "service_ids",
            columnDefinition = "uuid[]"
    )
    lateinit var serviceIds: List<UUID>

    @Type(type = "year")
    @Column(name = "startCollaboration", nullable = false)
    lateinit var startCollaboration: Year

    @Type(type = "year")
    @Column(name = "end_collaboration")
    var endCollaboration: Year? = null

    @Column(name = "industry_id")
    @Type(type = "pg-uuid")
    var industryId: UUID? = null

    @Column(name = "industry_name", length = 255)
    var industryName: String? = null

    @Column(name = "verification_stage", nullable = false)
    var verificationStage: ConnectionStatus = ConnectionStatus.Pending

    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    lateinit var data: Data

    @PrePersist
    fun prePersist() {
        sellerRoleName = data.sellerCompany.role.name
        buyerRoleName = data.buyerCompany.role.name
    }
//TODO Типы json перевести в сущности и использовать их в других соущностях
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
        var industry: Industry? = null
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



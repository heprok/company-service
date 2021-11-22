package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.companyservice.common.jpa.enumration.CompanyRoleTypeEnum
import com.briolink.companyservice.common.jpa.enumration.ConnectionStatusEnum
import com.fasterxml.jackson.annotation.JsonProperty
import com.vladmihalcea.hibernate.type.range.Range
import org.hibernate.annotations.Type
import java.net.URL
import java.time.Instant
import java.time.Year
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "connection", schema = "read")
@Entity
class ConnectionReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    val id: UUID
) : BaseReadEntity() {

    @Column(name = "participant_from_company_id", nullable = false)
    lateinit var participantFromCompanyId: UUID

    @Column(name = "participant_from_user_id", nullable = false)
    lateinit var participantFromUserId: UUID

    @Column(name = "participant_from_role_id", nullable = false)
    lateinit var participantFromRoleId: UUID

    @Column(name = "participant_from_role_name", nullable = false)
    lateinit var participantFromRoleName: String

    @Column(name = "participant_from_role_type", nullable = false)
    private var _participantFromRoleType = 0

    @Column(name = "participant_to_company_id", nullable = false)
    lateinit var participantToCompanyId: UUID

    @Column(name = "participant_to_user_id", nullable = false)
    lateinit var participantToUserId: UUID

    @Column(name = "participant_to_role_id", nullable = false)
    lateinit var participantToRoleId: UUID

    @Column(name = "participant_to_role_name", nullable = false)
    lateinit var participantToRoleName: String

    @Column(name = "participant_to_role_type", nullable = false)
    private var _participantToRoleType = 0

    @Type(type = "uuid-array")
    @Column(name = "service_ids", columnDefinition = "uuid[]")
    lateinit var serviceIds: List<UUID>

    @Column(name = "dates", columnDefinition = "int4range", nullable = false)
    lateinit var dates: Range<Int>

    @Column(name = "location")
    var location: String? = null

    @Column(name = "country")
    var country: String? = null

    @Column(name = "company_industry_id")
    var companyIndustryId: UUID? = null

    @Column(name = "status", nullable = false)
    private var _status: Int = ConnectionStatusEnum.Pending.value

    @Type(type = "uuid-array")
    @Column(name = "hidden_company_ids", columnDefinition = "uuid[]")
    lateinit var hiddenCompanyIds: List<UUID>

    @Type(type = "uuid-array")
    @Column(name = "deleted_company_ids", columnDefinition = "uuid[]")
    lateinit var deletedCompanyIds: List<UUID>

    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false)
    lateinit var data: Data

    var status: ConnectionStatusEnum
        get() = ConnectionStatusEnum.fromInt(_status)
        set(value) {
            _status = value.value
        }

    var participantFromRoleType: CompanyRoleTypeEnum
        get() = CompanyRoleTypeEnum.fromInt(_participantFromRoleType)
        set(value) {
            _participantFromRoleType = value.value
        }

    var participantToRoleType: CompanyRoleTypeEnum
        get() = CompanyRoleTypeEnum.fromInt(_participantToRoleType)
        set(value) {
            _participantToRoleType = value.value
        }

    data class Data(
        @JsonProperty
        val participantFrom: Participant,
        @JsonProperty
        val participantTo: Participant,
        @JsonProperty
        val services: ArrayList<Service>,
        @JsonProperty
        val industry: String?,
    )

    data class Participant(
        @JsonProperty
        val user: User,
        @JsonProperty
        val userJobPositionTitle: String?,
        @JsonProperty
        val company: Company,
        @JsonProperty
        val companyRole: CompanyRole,
    )

    data class CompanyRole(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val name: String,
        @JsonProperty
        val type: CompanyRoleTypeEnum
    )

    data class Service(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val serviceId: UUID?,
        @JsonProperty
        val serviceName: String,
        @JsonProperty
        val startDate: Year,
        @JsonProperty
        val endDate: Year?,
    )

    data class User(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val slug: String,
        @JsonProperty
        val image: URL?,
        @JsonProperty
        val lastName: String,
        @JsonProperty
        val firstName: String,
    )

    data class Company(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val slug: String,
        @JsonProperty
        val logo: URL?,
        @JsonProperty
        val name: String,
    )
}



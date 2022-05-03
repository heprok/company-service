package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.companyservice.common.jpa.enumeration.CompanyRoleTypeEnum
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.time.LocalDate
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "project", schema = "read")
class ProjectReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", length = 36, nullable = false)
    var id: UUID
) : BaseReadEntity() {
    @Type(type = "pg-uuid")
    @Column(name = "participant_from_company_id")
    lateinit var participantFromCompanyId: UUID

    @Type(type = "pg-uuid")
    @Column(name = "participant_from_user_id", nullable = false)
    lateinit var participantFromUserId: UUID

    @Column(name = "participant_from_role_type", nullable = false)
    private var _participantFromRoleType: Int = CompanyRoleTypeEnum.Seller.value

    @Column(name = "participant_from_role_name", nullable = false)
    lateinit var participantFromRoleName: String

    @Type(type = "pg-uuid")
    @Column(name = "participant_to_company_id", nullable = false)
    lateinit var participantToCompanyId: UUID

    @Type(type = "pg-uuid")
    @Column(name = "participant_to_user_id", nullable = false)
    lateinit var participantToUserId: UUID

    @Column(name = "participant_to_role_type", nullable = false)
    private var _participantToRoleType: Int = CompanyRoleTypeEnum.Buyer.value

    @Column(name = "participant_to_role_name", nullable = false)
    lateinit var participantToRoleName: String

    @Type(type = "uuid-array")
    @Column(name = "hidden_company_ids", columnDefinition = "uuid[]")
    var hiddenCompanyIds: Array<UUID> = arrayOf()

    @Type(type = "uuid-array")
    @Column(name = "accepted_company_ids", columnDefinition = "uuid[]")
    var acceptedCompanyIds: Array<UUID> = arrayOf()

    @Type(type = "uuid-array")
    @Column(name = "deleted_company_ids", columnDefinition = "uuid[]")
    var deletedCompanyIds: Array<UUID> = arrayOf()

    @Type(type = "pg-uuid")
    @Column(name = "service_id")
    lateinit var serviceId: UUID

    var participantFromRoleType: CompanyRoleTypeEnum
        get() = CompanyRoleTypeEnum.ofValue(_participantFromRoleType)
        set(value) {
            _participantFromRoleType = value.value
        }

    var participantToRoleType: CompanyRoleTypeEnum
        get() = CompanyRoleTypeEnum.ofValue(_participantToRoleType)
        set(value) {
            _participantToRoleType = value.value
        }

    @Type(type = "jsonb")
    @Column(name = "data", columnDefinition = "jsonb", nullable = false)
    lateinit var data: Data

    data class Data(
        @JsonProperty
        val service: ProjectService
    )

    data class ProjectService(
        @JsonProperty
        val service: Service,
        @JsonProperty
        val startDate: LocalDate,
        @JsonProperty
        val endDate: LocalDate? = null
    )

    data class Service(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val name: String,
        @JsonProperty
        val slug: String,
    )
}

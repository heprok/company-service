package com.briolink.companyservice.common.jpa.read.entity.company

import com.briolink.companyservice.common.enumeration.CompanyTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.BaseReadEntity
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.location.model.LocationMinInfo
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.time.Year
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "company", schema = "read")
@Entity
class CompanyReadEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID,

    @Column(name = "slug", nullable = false, length = 50)
    var slug: String,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Type(type = "persist-enum")
    @Column(name = "primary_company_type", nullable = false)
    var primaryCompanyType: CompanyTypeEnum,
) : BaseReadEntity() {

    @Type(type = "persist-enum-set")
    @Column(name = "company_types", nullable = false, columnDefinition = "int2[]")
    var companyTypes: List<CompanyTypeEnum> = listOf()

    @Column(name = "created_by")
    var createdBy: UUID? = null

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    lateinit var data: Data

    @Type(type = "jsonb")
    @Column(name = "sturtup_data", columnDefinition = "jsonb")
    var startupData: StartupData? = null

    @Type(type = "jsonb")
    @Column(name = "investor_data", columnDefinition = "jsonb")
    var investorData: InvestorData? = null

    @Type(type = "jsonb")
    @Column(name = "service_provider_data", columnDefinition = "jsonb")
    var spData: SPData? = null

    data class Data(
        @JsonProperty
        var info: BaseCompany,
        @JsonProperty
        var pbId: String?,
        @JsonProperty
        var parentCompany: BaseCompany?,
        @JsonProperty
        var website: URL?,
        @JsonProperty
        var familiarName: String?,
        @JsonProperty
        var description: String?,
        @JsonProperty
        var shortDescription: String?,
        @JsonProperty
        var location: LocationMinInfo?,
        @JsonProperty
        var keywords: MutableSet<Tag> = mutableSetOf(),
        @JsonProperty
        var verticals: MutableSet<Tag> = mutableSetOf(),
        @JsonProperty
        var primaryIndustry: Tag?,
        @JsonProperty
        var otherIndustries: MutableSet<Tag> = mutableSetOf(),
        @JsonProperty
        val yearFounded: Year?,
        @JsonProperty
        var facebook: String?,
        @JsonProperty
        var twitter: String?,
        @JsonProperty
        var employees: Int?,
    )

    data class BaseCompany(
        @JsonProperty
        var id: UUID,
        @JsonProperty
        var primaryType: CompanyTypeEnum,
        @JsonProperty
        var pbId: String?,
        @JsonProperty
        var slug: String,
        @JsonProperty
        var name: String,
        @JsonProperty
        var logo: URL?
    )
}

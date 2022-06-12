package com.briolink.companyservice.common.jpa.write.entity

import com.briolink.companyservice.common.enumeration.CompanyTypeEnum
import com.briolink.lib.common.type.basic.IntRange
import com.briolink.lib.common.type.basic.Money
import com.briolink.lib.common.type.basic.MoneyRange
import com.briolink.lib.common.type.basic.ValueWithCount
import com.briolink.lib.common.utils.StringUtils
import com.briolink.lib.location.enumeration.TypeLocationEnum
import com.briolink.lib.location.model.LocationId
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.PrePersist
import javax.persistence.Table

@Table(name = "company", schema = "write")
@Entity
class CompanyWriteEntity(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "slug", nullable = false, length = 255)
    var slug: String,

    @Type(type = "persist-enum")
    @Column(name = "primary_company_type", nullable = false, length = 255)
    var primaryCompanyType: CompanyTypeEnum,
) : BaseWriteEntity() {

    @Column(name = "pbId", length = 100)
    var pbId: String? = null

    @Column(name = "perent_company_id", length = 36)
    var parentCompanyId: UUID? = null

    @Column(name = "perent_company_pb_id", length = 100)
    var parentCompanyPbId: String? = null

    @Type(type = "persist-enum-set")
    @Column(name = "company_types", nullable = false, columnDefinition = "int2[]")
    var companyTypes: List<CompanyTypeEnum> = listOf()
        set(value) {
            field = if (value.contains(primaryCompanyType))
                mutableListOf<CompanyTypeEnum>().apply {
                    addAll(value)
                    remove(primaryCompanyType)
                }
            else
                value
        }

    @Column(name = "website", length = 255)
    private var website: String? = null

    @Column(name = "website_host", length = 255)
    private var websiteHost: String? = null

    @Column(name = "familiar_name")
    var familiarName: String? = null

    @Column(name = "logo")
    var logo: URL? = null

    @Column(name = "description")
    var description: String? = null

    @Column(name = "short_description", length = 255)
    var shortDescription: String? = null

    @Column(name = "hq_country_id")
    var hqCountryId: Int? = null

    @Column(name = "hq_state_id")
    var hqStateId: Int? = null

    @Column(name = "hq_city_id")
    var hqCityId: Int? = null

    @Type(type = "set-array")
    @Column(name = "keyword_ids", columnDefinition = "varchar[]")
    var keywordIds: MutableSet<String> = mutableSetOf()

    @Type(type = "set-array")
    @Column(name = "vertical_ids", columnDefinition = "varchar[]")
    var verticalIds: MutableSet<String> = mutableSetOf()

    @Column(name = "primary_industry_id")
    var primaryIndustryId: String? = null

    @Type(type = "set-array")
    @Column(name = "industry_ids", columnDefinition = "varchar[]")
    var industryIds: MutableSet<String> = mutableSetOf()

    @Column(name = "year_founded")
    var yearFounded: Int? = null

    @Column(name = "facebook")
    var facebook: String? = null

    @Column(name = "twitter")
    var twitter: String? = null

    @Column(name = "employees")
    var employees: Int? = null

    // TODO: Remove not null
    @Column(name = "created_by")
    @Type(type = "pg-uuid")
    var createdBy: UUID? = null

    @Type(type = "jsonb")
    @Column(name = "sturtup_data", columnDefinition = "jsonb")
    var startupData: StartupData? = null

    @Type(type = "jsonb")
    @Column(name = "investor_data", columnDefinition = "jsonb")
    var investorData: InvestorData? = null

    @Type(type = "jsonb")
    @Column(name = "service_provider_data", columnDefinition = "jsonb")
    var serviceProviderData: ServiceProviderData? = null

    data class InvestorData(
        @JsonProperty
        var financingStatusTagId: String? = null,
        @JsonProperty
        var primaryInvestorTypeTagId: String? = null,
        @JsonProperty
        var otherInvestorTypeTagIds: MutableSet<String> = mutableSetOf(),
        @JsonProperty
        var investorStatusTagId: String? = null,
        @JsonProperty
        var sic: SIC? = null,
        @JsonProperty
        var aum: Money? = null,
        @JsonProperty
        var dryPowder: Money? = null,
        @JsonProperty
        var investmentProfessionals: Int? = null,
        @JsonProperty
        var exits: Int? = null,
        @JsonProperty
        var activeInvestedCompanies: MutableSet<UUID> = mutableSetOf(),
        @JsonProperty
        var activeInvestedCompaniesCount: Int? = null,
        @JsonProperty
        var preferences: Preferences? = null,
        @JsonProperty
        var investments: Investments? = null,
        @JsonProperty
        var metrics: Metrics? = null
    )

    data class Investments(
        @JsonProperty
        var total: Int,
        @JsonProperty
        var last6Months: Int? = null,
        @JsonProperty
        var last12Months: Int? = null,
        @JsonProperty
        var last2Years: Int? = null,
        @JsonProperty
        var last5Years: Int? = null
    )

    data class Preferences(
        @JsonProperty
        var verticalTagIds: MutableSet<String> = mutableSetOf(),
        @JsonProperty
        var industryTagIds: MutableSet<String> = mutableSetOf(),
        @JsonProperty
        var regions: MutableSet<LocationId> = mutableSetOf(),
        @JsonProperty
        var realAssets: MutableSet<RealAssetItem> = mutableSetOf(),
        @JsonProperty
        var investmentTypeTagIds: MutableSet<String> = mutableSetOf(),
        @JsonProperty
        var investmentHorizon: IntRange? = null,
        @JsonProperty
        var otherTagIds: MutableSet<String> = mutableSetOf(),
        @JsonProperty
        var investmentAmount: MoneyRange? = null,
        @JsonProperty
        var dealSize: MoneyRange? = null,
        @JsonProperty
        var companyValuation: MoneyRange? = null,
        @JsonProperty
        var ebitda: MoneyRange? = null,
        @JsonProperty
        var ebit: MoneyRange? = null,
        @JsonProperty
        var revenue: MoneyRange? = null
    )

    data class RealAssetItem(
        @JsonProperty
        val classTagId: String,
        @JsonProperty
        val itemTagIds: Set<String>
    )

    data class Metrics(
        @JsonProperty
        var medianQuartile: String? = null,
        @JsonProperty
        var totalNumberOfFunds: Int? = null,
        @JsonProperty
        var topQuartileNumberOfFunds: Int? = null,
        @JsonProperty
        var upperMidQuartileNumberOfFunds: Int? = null,
        @JsonProperty
        var midQuartileNumberOfFunds: Int? = null,
        @JsonProperty
        var bottomQuartileNumberOfFunds: Int? = null
    )

    data class SIC(
        @JsonProperty
        val tagIds: MutableSet<String> = mutableSetOf(),
        @JsonProperty
        val codes: MutableSet<String> = mutableSetOf()
    )

    data class StartupData(
        @JsonProperty
        var totalPatentDocs: Int? = null,
        @JsonProperty
        var revenue: Money? = null,
        @JsonProperty
        var totalRaised: Money? = null,
        @JsonProperty
        var lastFinancing: Money? = null,
        @JsonProperty
        var lastFinancingStatusTagId: String? = null,
        @JsonProperty
        var universeTagIds: MutableSet<String> = mutableSetOf(),
        @JsonProperty
        var cpcCodeTagIds: MutableSet<String> = mutableSetOf(),
        @JsonProperty
        var investors: MutableSet<UUID> = mutableSetOf(),
        @JsonProperty
        var potentialInvestors: MutableSet<ValueWithCount<UUID, Double>> = mutableSetOf(),
        @JsonProperty
        var servicesProviders: MutableSet<UUID> = mutableSetOf(),
        @JsonProperty
        var otherServicesProviders: MutableSet<String> = mutableSetOf(),
        @JsonProperty
        var comparables: MutableSet<ValueWithCount<UUID, Double>> = mutableSetOf(),
        @JsonProperty
        var otherComparables: MutableSet<ValueWithCount<String, Double>> = mutableSetOf(),
    )

    data class ServiceProviderData(
        @JsonProperty
        var primaryTypeTagId: String? = null,
        @JsonProperty
        var otherTypeTagIds: MutableSet<String> = mutableSetOf(),
        @JsonProperty
        val serviced: ServiceProviderServiced? = null,
        @JsonProperty
        var primaryContact: ServiceProviderContact? = null,
        @JsonProperty
        val lastDeal: ServiceProviderDeal? = null,
        @JsonProperty
        var lastClosedFund: ServiceProviderFund? = null
    )

    data class ServiceProviderFund(
        @JsonProperty
        val name: String,
        @JsonProperty
        var leadPartners: MutableSet<ServiceProviderDealLeadPartner> = mutableSetOf(),

    )

    data class ServiceProviderDeal(
        @JsonProperty
        var date: LocalDate? = null,
        @JsonProperty
        var size: Money? = null,
        @JsonProperty
        var valuation: Money? = null,
        @JsonProperty
        var valuationStatusTagId: String? = null,
        @JsonProperty
        var typeTagId: String? = null,
        @JsonProperty
        var classTagId: String? = null,
        @JsonProperty
        var leadPartners: MutableSet<ServiceProviderDealLeadPartner> = mutableSetOf(),
    )

    data class ServiceProviderServiced(
        @JsonProperty
        val companies: Int? = null,
        @JsonProperty
        val deals: Int? = null,
        @JsonProperty
        val investors: Int? = null,
        @JsonProperty
        val funds: Int? = null,
    ) {
        val total: Int
            get() = (companies ?: 0) + (deals ?: 0) + (investors ?: 0) + (funds ?: 0)
    }

    data class ServiceProviderContact(
        @JsonProperty
        var name: String,
        @JsonProperty
        var title: String? = null,
        @JsonProperty
        var email: String? = null,
        @JsonProperty
        var phone: String? = null,
        @JsonProperty
        var pbId: String? = null,
        @JsonProperty
        var personId: UUID? = null,
        @JsonProperty
        var personSlug: String? = null,
    )

    data class ServiceProviderDealLeadPartner(
        @JsonProperty
        var pbId: String? = null,
        @JsonProperty
        var name: String? = null,
    )

    var websiteUrl: URL?
        get() = website?.let { URL(it) }
        set(value) {
            websiteHost = value?.host
            website = value?.toString()
        }

    fun getLocationId(): LocationId? {
        return if (hqCityId != null)
            LocationId(
                id = hqCityId!!,
                type = TypeLocationEnum.City,
            )
        else if (hqStateId != null)
            LocationId(
                id = hqStateId!!,
                type = TypeLocationEnum.State,
            )
        else if (hqCountryId != null)
            LocationId(
                id = hqCountryId!!,
                type = TypeLocationEnum.Country,
            )
        else null
    }

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    @UpdateTimestamp
    @Column(name = "changed")
    var changed: Instant? = null

    @PrePersist
    fun prePersist() {
        if (slug == "") slug = StringUtils.slugify("$name ", true)
    }
}

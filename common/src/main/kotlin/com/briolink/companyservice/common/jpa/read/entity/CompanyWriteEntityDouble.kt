// package com.briolink.companyservice.common.jpa.read.entity
//
// import com.briolink.companyservice.common.enumeration.CompanyTypeEnum
// import com.briolink.lib.common.type.basic.DoubleRange
// import com.briolink.lib.common.type.basic.IntRange
// import com.briolink.lib.common.utils.StringUtils
// import com.briolink.lib.dictionary.model.Tag
// import com.briolink.lib.location.enumeration.TypeLocationEnum
// import com.briolink.lib.location.model.LocationFullInfo
// import com.briolink.lib.location.model.LocationId
// import com.fasterxml.jackson.annotation.JsonProperty
// import org.hibernate.annotations.CreationTimestamp
// import org.hibernate.annotations.Type
// import org.hibernate.annotations.UpdateTimestamp
// import java.net.URL
// import java.time.Instant
// import java.time.LocalDate
// import java.util.UUID
// import javax.persistence.Column
// import javax.persistence.Entity
// import javax.persistence.PrePersist
// import javax.persistence.Table
//
// @Table(name = "company", schema = "write")
// @Entity
// class CompanyWriteEntityDouble(
//     @Column(name = "name", nullable = false)
//     var name: String,
//
//     @Column(name = "slug", nullable = false, length = 255)
//     var slug: String,
//
//     @Type(type = "persist-enum")
//     @Column(name = "primary_company_type", nullable = false, length = 255)
//     var primaryCompanyType: CompanyTypeEnum,
// ) : BaseWriteEntity() {
//
//     @Column(name = "pbId", length = 100)
//     var pbId: String? = null
//
//     @Column(name = "perent_company_id", length = 36)
//     var parentCompanyId: UUID? = null
//
//     @Column(name = "perent_company_pb_id", length = 100)
//     var parentCompanyPbId: String? = null
//
//     @Type(type = "persist-enum-set")
//     @Column(name = "company_types", nullable = false, columnDefinition = "int2[]")
//     var companyTypes: Set<CompanyTypeEnum> = setOf()
//         set(value) {
//             if (value.contains(primaryCompanyType))
//                 field = mutableSetOf<CompanyTypeEnum>().apply {
//                     addAll(value)
//                     remove(primaryCompanyType)
//                 }
//             else
//                 field = value
//         }
//
//     @Column(name = "website", length = 255)
//     private var website: String? = null
//
//     @Column(name = "website_host", length = 255)
//     private var websiteHost: String? = null
//
//     @Column(name = "familiar_name")
//     var familiarName: String? = null
//
//     @Column(name = "logo")
//     var logo: URL? = null
//
//     @Column(name = "description")
//     var description: String? = null
//
//     @Column(name = "short_description", length = 255)
//     var shortDescription: String? = null
//
//     @Column(name = "hq_country_id")
//     var hqCountryId: Int? = null
//
//     @Column(name = "hq_state_id")
//     var hqStateId: Int? = null
//
//     @Column(name = "hq_city_id")
//     var hqCityId: Int? = null
//
//     @Type(type = "set-array")
//     @Column(name = "keyword_ids", columnDefinition = "varchar[]")
//     var keywordIds: MutableSet<String> = mutableSetOf()
//
//     @Type(type = "set-array")
//     @Column(name = "vertical_ids", columnDefinition = "varchar[]")
//     var verticalIds: MutableSet<String> = mutableSetOf()
//
//     @Type(type = "ltree")
//     @Column(name = "primary_industry_path", columnDefinition = "ltree")
//     var primaryIndustryPath: String? = null
//
//     @Type(type = "ltree-set")
//     @Column(name = "industry_paths", columnDefinition = "ltree[]")
//     var industryPaths: MutableSet<String> = mutableSetOf()
//
//     @Column(name = "year_founded")
//     var yearFounded: Int? = null
//
//     @Column(name = "facebook")
//     var facebook: String? = null
//
//     @Column(name = "twitter")
//     var twitter: String? = null
//
//     @Column(name = "employees")
//     var employees: Int? = null
//
//     // TODO: Remove not null
//     @Column(name = "created_by")
//     @Type(type = "pg-uuid")
//     var createdBy: UUID? = null
//
//     @Type(type = "jsonb")
//     @Column(name = "data", nullable = false)
//     var data: Data = Data()
//
//     @Type(type = "jsonb")
//     @Column(name = "sturtup_data", columnDefinition = "jsonb")
//     var startupData: StartupData? = null
//
//     @Type(type = "jsonb")
//     @Column(name = "investor_data", columnDefinition = "jsonb")
//     var investorData: InvestorData? = null
//
//     @Type(type = "jsonb")
//     @Column(name = "service_provider_data", columnDefinition = "jsonb")
//     var serviceProviderData: ServiceProviderData? = null
//
//     data class Data(
//         @JsonProperty
//         var hqLocation: LocationFullInfo? = null,
//         @JsonProperty
//         var primaryIndustry: Tag? = null,
//         @JsonProperty
//         var industries: MutableSet<Tag> = mutableSetOf(),
//         @JsonProperty
//         var verticals: MutableSet<Tag> = mutableSetOf(),
//         @JsonProperty
//         var keywords: MutableSet<Tag> = mutableSetOf(),
//     )
//
//     data class InvestorData(
//         @JsonProperty
//         var financingStatus: Tag? = null,
//         @JsonProperty
//         var primaryInvestorType: Tag? = null,
//         @JsonProperty
//         var otherInvestorTypes: MutableSet<Tag> = mutableSetOf(),
//         @JsonProperty
//         var investorStatus: String? = null,
//         @JsonProperty
//         var sic: SIC? = null,
//         @JsonProperty
//         var aum: Double? = null,
//         @JsonProperty
//         var dryPowder: Double? = null,
//         @JsonProperty
//         var investmentProfessionals: Int? = null,
//         @JsonProperty
//         var exits: Int? = null,
//         @JsonProperty
//         var activeInvestedCompanies: MutableSet<UUID> = mutableSetOf(),
//         @JsonProperty
//         var activeInvestedCompaniesCount: Int = 0,
//         @JsonProperty
//         var preferences: Preferences = Preferences(),
//         @JsonProperty
//         var investments: Investments? = null,
//         @JsonProperty
//         var metrics: Metrics = Metrics()
//     )
//
//     data class Investments(
//         @JsonProperty
//         var total: Int,
//         @JsonProperty
//         var last6Months: Int? = null,
//         @JsonProperty
//         var last12Months: Int? = null,
//         @JsonProperty
//         var last2Years: Int? = null,
//         @JsonProperty
//         var last5Years: Int? = null
//     )
//
//     data class Preferences(
//         @JsonProperty
//         var verticals: MutableSet<Tag> = mutableSetOf(),
//         @JsonProperty
//         var industries: MutableSet<Tag> = mutableSetOf(),
//         @JsonProperty
//         var regions: MutableSet<String> = mutableSetOf(),
//         @JsonProperty
//         var realAssets: MutableList<RealAssetItem> = mutableListOf(),
//         @JsonProperty
//         var investmentTypes: MutableSet<String> = mutableSetOf(),
//         @JsonProperty
//         var investmentHorizon: IntRange? = null,
//         @JsonProperty
//         var other: MutableSet<String> = mutableSetOf(),
//         @JsonProperty
//         var investmentAmount: DoubleRange? = null,
//         @JsonProperty
//         var dealSize: DoubleRange? = null,
//         @JsonProperty
//         var companyValuation: DoubleRange? = null,
//         @JsonProperty
//         var ebitda: DoubleRange? = null,
//         @JsonProperty
//         var ebit: DoubleRange? = null,
//         @JsonProperty
//         var revenue: DoubleRange? = null
//     )
//
//     data class RealAssetItem(
//         @JsonProperty
//         val `class`: String,
//         @JsonProperty
//         val items: Set<Tag>
//     )
//
//     data class Metrics(
//         @JsonProperty
//         var medianQuartile: String? = null,
//         @JsonProperty
//         var totalNumberOfFunds: Int? = null,
//         @JsonProperty
//         var topQuartileNumberOfFunds: Int? = null,
//         @JsonProperty
//         var upperMidQuartileNumberOfFunds: Int? = null,
//         @JsonProperty
//         var midQuartileNumberOfFunds: Int? = null,
//         @JsonProperty
//         var bottomQuartileNumberOfFunds: Int? = null
//     )
//
//     data class SIC(
//         @JsonProperty
//         val sectorDescriptions: MutableSet<Tag> = mutableSetOf(),
//         @JsonProperty
//         val groupDescriptions: MutableSet<Tag> = mutableSetOf(),
//         @JsonProperty
//         val codeDescriptions: MutableSet<Tag> = mutableSetOf(),
//         @JsonProperty
//         val codes: MutableSet<Tag> = mutableSetOf()
//     )
//
//     data class StartupData(
//         @JsonProperty
//         var totalPatentDocs: Int? = null,
//         @JsonProperty
//         var revenue: Double? = null,
//         @JsonProperty
//         var totalRaised: Double? = null,
//         @JsonProperty
//         var lastFinancing: Double? = null,
//         @JsonProperty
//         var lastFinancingStatus: Tag? = null,
//         @JsonProperty
//         var universe: MutableSet<Tag> = mutableSetOf(),
//         @JsonProperty
//         var cpcCodes: MutableSet<Tag> = mutableSetOf(),
//         @JsonProperty
//         var investors: MutableSet<UUID> = mutableSetOf(),
//         @JsonProperty
//         var servicesProviders: MutableSet<UUID> = mutableSetOf(),
//         @JsonProperty
//         var competitors: MutableSet<UUID> = mutableSetOf(),
//     )
//
//     data class ServiceProviderData(
//         @JsonProperty
//         var primaryType: Tag? = null,
//         @JsonProperty
//         var otherTypes: MutableSet<Tag> = mutableSetOf(),
//         @JsonProperty
//         var servicedCompanies: Int? = null,
//         @JsonProperty
//         var servicedDeals: Int? = null,
//         @JsonProperty
//         var servicedInvestors: Int? = null,
//         @JsonProperty
//         var servicedFunds: Int? = null,
//         @JsonProperty
//         var primaryContact: PrimaryContact? = null,
//         @JsonProperty
//         var lastDealDate: LocalDate? = null,
//         @JsonProperty
//         var lastDealSize: Double? = null,
//         @JsonProperty
//         var lastDealValuation: Double? = null,
//         @JsonProperty
//         var lastDealValuationStatus: Tag? = null,
//         @JsonProperty
//         var lastDealType1: Tag? = null,
//         @JsonProperty
//         var lastDealType2: Tag? = null,
//         @JsonProperty
//         var lastDealType3: Tag? = null,
//         @JsonProperty
//         var lastDealClass: Tag? = null,
//         @JsonProperty
//         var lastDealLeadPartners: MutableSet<DealLeadPartner> = mutableSetOf(),
//         @JsonProperty
//         var lastClosedFundName: String? = null,
//         @JsonProperty
//         var lastClosedFundLeadPartners: MutableSet<DealLeadPartner> = mutableSetOf(),
//     )
//
//     data class PrimaryContact(
//         @JsonProperty
//         var name: String,
//         @JsonProperty
//         var title: String? = null,
//         @JsonProperty
//         var email: String? = null,
//         @JsonProperty
//         var phone: String? = null,
//         @JsonProperty
//         var pbId: String? = null,
//         @JsonProperty
//         var personId: String? = null,
//         @JsonProperty
//         var personSlug: String? = null,
//     )
//
//     data class DealLeadPartner(
//         @JsonProperty
//         var pbId: String? = null,
//         @JsonProperty
//         var name: String? = null,
//     )
//
//     var websiteUrl: URL?
//         get() = website?.let { URL(it) }
//         set(value) {
//             websiteHost = value?.host
//             website = value?.toString()
//         }
//
//     fun getLocationId(): LocationId? {
//         return if (hqCityId != null)
//             LocationId(
//                 id = hqCityId!!,
//                 type = TypeLocationEnum.City,
//             )
//         else if (hqStateId != null)
//             LocationId(
//                 id = hqStateId!!,
//                 type = TypeLocationEnum.State,
//             )
//         else if (hqCountryId != null)
//             LocationId(
//                 id = hqCountryId!!,
//                 type = TypeLocationEnum.Country,
//             )
//         else null
//     }
//
//     @CreationTimestamp
//     @Column(name = "created", nullable = false)
//     lateinit var created: Instant
//
//     @UpdateTimestamp
//     @Column(name = "changed")
//     var changed: Instant? = null
//
//     @PrePersist
//     fun prePersist() {
//         if (slug == "") slug = StringUtils.slugify("$name ", true)
//     }
// }

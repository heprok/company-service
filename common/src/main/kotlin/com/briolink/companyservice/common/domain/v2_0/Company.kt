package com.briolink.companyservice.common.domain.v2_0

import com.briolink.lib.common.type.basic.IntRange
import com.briolink.lib.common.type.basic.Money
import com.briolink.lib.common.type.basic.MoneyRange
import com.briolink.lib.common.type.basic.ValueWithCount
import com.briolink.lib.location.model.LocationId
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

enum class CompanyType(@JsonValue val value: Int) {
    Startup(1),
    Investor(2),
    ServicesProvider(3);

    companion object {
        private val map = values().associateBy(CompanyType::value)
        fun ofValue(value: Int): CompanyType = map[value]!!
    }
}

data class Company(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String,
    @JsonProperty
    val slug: String,
    @JsonProperty
    val primaryCompanyType: CompanyType,
    @JsonProperty
    val pbId: String?,
    @JsonProperty
    val parentCompanyId: UUID?,
    @JsonProperty
    val parentCompanyPbId: String?,
    @JsonProperty
    val companyTypes: ArrayList<CompanyType>,
    @JsonProperty
    val website: URL?,
    @JsonProperty
    val familiarName: String?,
    @JsonProperty
    val logo: URL?,
    @JsonProperty
    val description: String?,
    @JsonProperty
    val shortDescription: String?,
    @JsonProperty
    val locationId: LocationId?,
    @JsonProperty
    val keywordTagIds: ArrayList<String>,
    @JsonProperty
    val verticalTagIds: ArrayList<String>,
    @JsonProperty
    val primaryIndustryTagId: String?,
    @JsonProperty
    val otherIndustryTagIds: ArrayList<String>,
    @JsonProperty
    val yearFounded: Int?,
    @JsonProperty
    val facebook: String?,
    @JsonProperty
    val twitter: String?,
    @JsonProperty
    val employees: Int?,
    @JsonProperty
    val createdBy: UUID?,
    @JsonProperty
    val investorInfo: InvestorInfo?,
    @JsonProperty
    val startupInfo: StartupInfo?,
    @JsonProperty
    val servicesProviderData: ServicesProviderInfo?,
    @JsonProperty
    val changed: Instant?,
    @JsonProperty
    val created: Instant,
) : Domain {
    val companyIds = mutableSetOf<UUID>().apply {
        parentCompanyId?.also { add(it) }
        startupInfo?.potentialInvestors?.also { addAll(it.map { it.value }) }
        startupInfo?.servicesProviders?.also { addAll(it) }
        startupInfo?.comparables?.also { addAll(it.map { it.value }) }
        startupInfo?.investors?.also { addAll(it) }

        investorInfo?.activeInvestedCompanies?.also { addAll(it) }
    }

    val tagIds = mutableSetOf<String>().apply {
        addAll(keywordTagIds)
        addAll(verticalTagIds)
        addAll(otherIndustryTagIds)
        primaryIndustryTagId?.also { add(it) }

        servicesProviderData?.primaryTypeTagId?.also { add(it) }
        servicesProviderData?.otherTypeTagIds?.also { addAll(it) }
        servicesProviderData?.lastDeal?.typeTagId?.also { add(it) }
        servicesProviderData?.lastDeal?.valuationStatusTagId?.also { add(it) }
        servicesProviderData?.lastDeal?.classTagId?.also { add(it) }

        startupInfo?.universeTagIds?.also { addAll(it) }
        startupInfo?.cpcCodesTagIds?.also { addAll(it) }

        investorInfo?.financingStatusTagId?.also { add(it) }
        investorInfo?.primaryInvestorTypeTagId?.also { add(it) }
        investorInfo?.otherInvestorTypeTagIds?.also { addAll(it) }
        investorInfo?.investorStatusTagId?.also { add(it) }
        investorInfo?.sic?.tagsIds?.also { addAll(it) }

        investorInfo?.preferences?.verticalTagIds?.also { addAll(it) }
        investorInfo?.preferences?.industryTagIds?.also { addAll(it) }
        investorInfo?.preferences?.realAssets?.forEach {
            addAll(it.itemTagIds)
            add(it.classTagId)
        }
        investorInfo?.preferences?.investmentTypeTagIds?.also { addAll(it) }
        investorInfo?.preferences?.otherTagIds?.also { addAll(it) }
    }
}

data class ServicesProviderInfo(
    @JsonProperty
    var primaryTypeTagId: String?,
    @JsonProperty
    var otherTypeTagIds: ArrayList<String>,
    @JsonProperty
    var serviced: ServiceProviderServicedData?,
    @JsonProperty
    var primaryContact: PrimaryContactData?,
    @JsonProperty
    var lastDeal: ServiceProviderDealData?,
    @JsonProperty
    var lastClosedFund: ServiceProviderFundData?,
)

data class ServiceProviderServicedData(
    @JsonProperty
    val total: Int,
    @JsonProperty
    val companies: Int? = null,
    @JsonProperty
    val deals: Int? = null,
    @JsonProperty
    val investors: Int? = null,
    @JsonProperty
    val funds: Int? = null,
)

data class ServiceProviderFundData(
    @JsonProperty
    val name: String,
    @JsonProperty
    val leadPartners: ArrayList<DealLeadPartnerData>,
)

data class PrimaryContactData(
    @JsonProperty
    var name: String,
    @JsonProperty
    var title: String?,
    @JsonProperty
    var email: String?,
    @JsonProperty
    var phone: String?,
    @JsonProperty
    var pbId: String?,
    @JsonProperty
    var personId: UUID?,
    @JsonProperty
    var personSlug: String?,
)

data class ServiceProviderDealData(
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
    var leadPartners: ArrayList<DealLeadPartnerData>
)

data class DealLeadPartnerData(
    @JsonProperty
    var pbId: String?,
    @JsonProperty
    var name: String?,
)

data class StartupInfo(
    @JsonProperty
    var totalPatentDocs: Int?,
    @JsonProperty
    var revenue: Money?,
    @JsonProperty
    var totalRaised: Money?,
    @JsonProperty
    var lastFinancing: Money?,
    @JsonProperty
    var lastFinancingStatusTagId: String?,
    @JsonProperty
    var universeTagIds: ArrayList<String>,
    @JsonProperty
    var cpcCodesTagIds: ArrayList<String>,
    @JsonProperty
    var investors: ArrayList<UUID>,
    @JsonProperty
    val potentialInvestors: ArrayList<ValueWithCount<UUID, Double>>,
    @JsonProperty
    val servicesProviders: ArrayList<UUID>,
    @JsonProperty
    val otherServicesProviders: ArrayList<String>,
    @JsonProperty
    val comparables: ArrayList<ValueWithCount<UUID, Double>>,
    @JsonProperty
    val otherComparables: ArrayList<ValueWithCount<String, Double>>,
)

data class InvestorInfo(
    @JsonProperty
    var financingStatusTagId: String?,
    @JsonProperty
    var primaryInvestorTypeTagId: String?,
    @JsonProperty
    var otherInvestorTypeTagIds: ArrayList<String>,
    @JsonProperty
    var investorStatusTagId: String?,
    @JsonProperty
    var sic: SICData?,
    @JsonProperty
    var aum: Money?,
    @JsonProperty
    var dryPowder: Money?,
    @JsonProperty
    var investmentProfessionals: Int?,
    @JsonProperty
    var exits: Int?,
    @JsonProperty
    var activeInvestedCompanies: ArrayList<UUID>,
    @JsonProperty
    var activeInvestedCompaniesCount: Int?,
    @JsonProperty
    var preferences: PreferencesData?,
    @JsonProperty
    var investments: InvestmentsData?,
    @JsonProperty
    var metrics: MetricsData?
)

data class InvestmentsData(
    @JsonProperty
    var total: Int,
    @JsonProperty
    var last6Months: Int?,
    @JsonProperty
    var last12Months: Int?,
    @JsonProperty
    var last2Years: Int?,
    @JsonProperty
    var last5Years: Int?
)

data class PreferencesData(
    @JsonProperty
    var verticalTagIds: ArrayList<String>,
    @JsonProperty
    var industryTagIds: ArrayList<String>,
    @JsonProperty
    var regions: ArrayList<LocationId>,
    @JsonProperty
    var realAssets: ArrayList<RealAssetItemData>,
    @JsonProperty
    var investmentTypeTagIds: ArrayList<String>,
    @JsonProperty
    var investmentHorizon: IntRange?,
    @JsonProperty
    var otherTagIds: ArrayList<String>,
    @JsonProperty
    var investmentAmount: MoneyRange?,
    @JsonProperty
    var dealSize: MoneyRange?,
    @JsonProperty
    var companyValuation: MoneyRange?,
    @JsonProperty
    var ebitda: MoneyRange?,
    @JsonProperty
    var ebit: MoneyRange?,
    @JsonProperty
    var revenue: MoneyRange?
)

data class RealAssetItemData(
    @JsonProperty
    val classTagId: String,
    @JsonProperty
    val itemTagIds: ArrayList<String>
)

data class MetricsData(
    @JsonProperty
    var medianQuartile: String?,
    @JsonProperty
    var totalNumberOfFunds: Int?,
    @JsonProperty
    var topQuartileNumberOfFunds: Int?,
    @JsonProperty
    var upperMidQuartileNumberOfFunds: Int?,
    @JsonProperty
    var midQuartileNumberOfFunds: Int?,
    @JsonProperty
    var bottomQuartileNumberOfFunds: Int?
)

data class SICData(
    @JsonProperty
    val tagsIds: ArrayList<String>,
    @JsonProperty
    val codes: ArrayList<String>
)

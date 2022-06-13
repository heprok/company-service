package com.briolink.companyservice.common.jpa.read.entity.company

import com.briolink.lib.common.type.basic.IntRange
import com.briolink.lib.common.type.basic.Money
import com.briolink.lib.common.type.basic.MoneyRange
import com.briolink.lib.dictionary.enumeration.TagType
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.dictionary.validation.TagTypeMatch
import com.briolink.lib.dictionary.validation.TagTypeMatchInCollection
import com.briolink.lib.location.model.LocationMinInfo
import com.fasterxml.jackson.annotation.JsonProperty

data class InvestorData(
    @JsonProperty
    @TagTypeMatch([TagType.FinancingStatus])
    var financingStatus: Tag? = null,
    @JsonProperty
    @TagTypeMatch([TagType.InvestorType])
    var primaryInvestorType: Tag? = null,
    @JsonProperty
    @TagTypeMatchInCollection([TagType.InvestorType])
    var otherInvestorTypes: MutableSet<Tag> = mutableSetOf(),
    @JsonProperty
    @TagTypeMatchInCollection([TagType.InvestorStatus])
    var investorStatus: Tag? = null,
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
    var activeInvestedCompanies: MutableSet<CompanyReadEntity.BaseCompany> = mutableSetOf(),
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
    @TagTypeMatchInCollection([TagType.Vertical])
    var verticals: MutableSet<Tag> = mutableSetOf(),
    @JsonProperty
    @TagTypeMatchInCollection([TagType.Industry])
    var industries: MutableSet<Tag> = mutableSetOf(),
    @JsonProperty
    var regions: MutableSet<LocationMinInfo> = mutableSetOf(),
    @JsonProperty
    var realAssets: MutableSet<RealAssetItem> = mutableSetOf(),
    @JsonProperty
    @TagTypeMatchInCollection([TagType.InvestmentType])
    var investmentTypes: MutableSet<Tag> = mutableSetOf(),
    @JsonProperty
    var investmentHorizon: IntRange? = null,
    @JsonProperty
    @TagTypeMatchInCollection([TagType.OtherStated])
    var others: MutableSet<Tag> = mutableSetOf(),
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
    @TagTypeMatch([TagType.RealAssetType])
    val `class`: Tag,
    @JsonProperty
    @TagTypeMatchInCollection([TagType.RealAssetType])
    val item: Set<Tag>
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
    @TagTypeMatchInCollection([TagType.SIC])
    val tags: MutableSet<Tag> = mutableSetOf(),
    @JsonProperty
    val codes: MutableSet<String> = mutableSetOf()
)

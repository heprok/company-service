package com.briolink.companyservice.api.service.company.dto

import com.briolink.lib.common.type.basic.IntRange
import com.briolink.lib.common.type.basic.Money
import com.briolink.lib.common.type.basic.MoneyRange
import com.briolink.lib.dictionary.enumeration.TagType
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.dictionary.validation.TagTypeMatch
import com.briolink.lib.dictionary.validation.TagTypeMatchInCollection
import com.briolink.lib.location.model.LocationId
import java.util.UUID

data class CreateInvestorInfoDto(
    @TagTypeMatch([TagType.FinancingStatus])
    val financingStatus: Tag?,
    @TagTypeMatch([TagType.InvestorType])
    val primaryInvestorType: Tag?,
    @TagTypeMatchInCollection([TagType.InvestorType])
    val otherInvestorTypes: MutableSet<Tag> = mutableSetOf(),
    @TagTypeMatch([TagType.InvestorStatus])
    val investorStatus: Tag?,
    val sic: SICDto?,
    val aum: Money?,
    val dryPowder: Money?,
    val investmentProfessionals: Int?,
    val exits: Int?,
    val activeInvestedCompanies: MutableSet<UUID> = mutableSetOf(),
    val activeInvestedCompaniesCount: Int?,
    val preferences: PreferencesDto?,
    val investments: InvestmentDto?,
    val metrics: MetricsDto?,
) {
    fun getAllTags(): Set<Tag> =
        mutableSetOf<Tag>().apply {
            financingStatus?.also { add(it) }
            primaryInvestorType?.also { add(it) }
            otherInvestorTypes.also { addAll(it) }
            investorStatus?.also { add(it) }
            sic?.getAllTags()?.also { addAll(it) }
            preferences?.getAllTags()?.also { addAll(it) }
        }
}

data class MetricsDto(
    val medianQuartile: String?,
    val totalNumberOfFunds: Int?,
    val topQuartileNumberOfFunds: Int?,
    val upperMidQuartileNumberOfFunds: Int?,
    val midQuartileNumberOfFunds: Int?,
    val bottomQuartileNumberOfFunds: Int?
)

data class InvestmentDto(
    val total: Int,
    val last6Months: Int?,
    val last12Months: Int?,
    val last2Years: Int?,
    val last5Years: Int?,
)

data class PreferencesDto(
    @TagTypeMatchInCollection([TagType.Vertical])
    val verticals: MutableSet<Tag> = mutableSetOf(),
    @TagTypeMatchInCollection([TagType.Industry])
    val industries: MutableSet<Tag> = mutableSetOf(),
    val regions: MutableSet<LocationId> = mutableSetOf(),
    val realAssets: MutableSet<RealAssetItemDto> = mutableSetOf(),
    @TagTypeMatchInCollection([TagType.InvestmentType])
    val investmentTypes: MutableSet<Tag> = mutableSetOf(),
    val investmentHorizon: IntRange?,
    @TagTypeMatchInCollection([TagType.OtherStated])
    val other: MutableSet<Tag> = mutableSetOf(),
    val investmentAmount: MoneyRange?,
    val dealSize: MoneyRange?,
    val companyValuation: MoneyRange?,
    val ebitda: MoneyRange?,
    val ebit: MoneyRange?,
    val revenue: MoneyRange?,
) {
    fun getAllTags(): Set<Tag> =
        mutableSetOf<Tag>().apply {
            verticals.also { addAll(it) }
            industries.also { addAll(it) }
            investmentTypes.also { addAll(it) }
            realAssets.map { it.getAllTags() }.onEach { addAll(it) }
            other.also { addAll(it) }
        }
}

data class RealAssetItemDto(
    @TagTypeMatch([TagType.RealAssetType])
    val `class`: Tag,
    @TagTypeMatchInCollection([TagType.RealAssetType])
    val items: MutableSet<Tag> = mutableSetOf()
) {
    fun getAllTags(): Set<Tag> =
        mutableSetOf<Tag>().apply {
            `class`.also { add(it) }
            items.also { addAll(it) }
        }
}

data class SICDto(
    @TagTypeMatchInCollection([TagType.SIC])
    val tags: MutableSet<Tag> = mutableSetOf(),
    val codes: MutableSet<String> = mutableSetOf(),
) {
    fun getAllTags(): Set<Tag> =
        mutableSetOf<Tag>().apply {
            tags.also { addAll(it) }
        }
}

package com.briolink.companyservice.api.service.company.dto.mapper

import com.briolink.companyservice.api.graphql.mapper.toFilterMutableSetWithUUID
import com.briolink.companyservice.api.graphql.mapper.toMoney
import com.briolink.companyservice.api.graphql.mapper.toMutableSetTags
import com.briolink.companyservice.api.graphql.mapper.toRangeMoney
import com.briolink.companyservice.api.graphql.mapper.toTag
import com.briolink.companyservice.api.service.company.dto.CreateInvestorInfoDto
import com.briolink.companyservice.api.service.company.dto.InvestmentDto
import com.briolink.companyservice.api.service.company.dto.MetricsDto
import com.briolink.companyservice.api.service.company.dto.PreferencesDto
import com.briolink.companyservice.api.service.company.dto.RealAssetItemDto
import com.briolink.companyservice.api.service.company.dto.SICDto
import com.briolink.companyservice.api.types.CreateInvestmentInput
import com.briolink.companyservice.api.types.CreateInvestorInfoInput
import com.briolink.companyservice.api.types.CreateMetricsInput
import com.briolink.companyservice.api.types.CreatePreferencesInput
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.location.model.LocationId

fun CreateInvestorInfoInput.toDto() = CreateInvestorInfoDto(
    financingStatus = financingStatus?.toTag(),
    primaryInvestorType = primaryInvestorType?.toTag(),
    otherInvestorTypes = otherInvestorTypes?.map { it.toTag() }?.toMutableSet() ?: mutableSetOf(),

    investorStatus = investorStatus?.toTag(),
    sic = sic?.let { sicInput ->
        val tags = mutableSetOf<Tag>()
        sicInput.codeDescriptions.forEach {
            it.toTag().apply {
                if (path == null) throw IllegalArgumentException("SIC code description must be not null")

                val pCode = path!!.replaceAfterLast('.', "").dropLast(1)

                parent = sicInput.groupDescriptions.find { g -> g.path == pCode }?.toTag()
                    ?: throw IllegalArgumentException("SIC Group not found")

                val pSector = parent!!.path?.replaceAfterLast('.', "")?.dropLast(1)
                    ?: throw IllegalArgumentException("SIC Group must be with path")

                parent!!.parent = sicInput.sectorDescriptions.find { s -> s.path == pSector }?.toTag()
                    ?: throw IllegalArgumentException("SIC Sector not found")

                tags.add(this)
            }
        }

        SICDto(tags, sicInput.codes.toMutableSet())
    },
    aum = aum?.toMoney(),
    dryPowder = dryPowder?.toMoney(),
    investmentProfessionals = investmentProfessionals,
    exits = exits,
    activeInvestedCompanies = activeInvestedCompanies?.toFilterMutableSetWithUUID() ?: mutableSetOf(),
    activeInvestedCompaniesCount = activeInvestedCompaniesCount,
    preferences = preferences?.toDto(),
    investments = investments?.toDto(),
    metrics = metrics?.toDto(),
)

fun CreatePreferencesInput.toDto() = PreferencesDto(
    verticals = verticals?.toMutableSetTags() ?: mutableSetOf(),
    industries = industries?.toMutableSetTags() ?: mutableSetOf(),
    regions = regions?.map { LocationId.fromString(it) }?.toMutableSet() ?: mutableSetOf(),
    realAssets = realAssets?.map {
        RealAssetItemDto(
            `class` = it.`class`.toTag(),
            items = it.items.toMutableSetTags()
        )
    }?.toMutableSet() ?: mutableSetOf(),
    investmentHorizon = null,
    investmentTypes = investmentTypes?.toMutableSetTags() ?: mutableSetOf(),
    other = other?.toMutableSetTags() ?: mutableSetOf(),
    investmentAmount = investmentAmount?.toRangeMoney(),
    dealSize = dealSize?.toRangeMoney(),
    companyValuation = companyValuation?.toRangeMoney(),
    ebitda = ebitda?.toRangeMoney(),
    ebit = ebit?.toRangeMoney(),
    revenue = revenue?.toRangeMoney(),
)

fun CreateInvestmentInput.toDto() = InvestmentDto(
    total = total,
    last6Months = last6Months,
    last12Months = last12Months,
    last2Years = last2Years,
    last5Years = last5Years
)

fun CreateMetricsInput.toDto() = MetricsDto(
    medianQuartile = medianQuartile,
    totalNumberOfFunds = totalNumberOfFunds,
    topQuartileNumberOfFunds = topQuartileNumberOfFunds,
    upperMidQuartileNumberOfFunds = upperMidQuartileNumberOfFunds,
    midQuartileNumberOfFunds = midQuartileNumberOfFunds,
    bottomQuartileNumberOfFunds = bottomQuartileNumberOfFunds

)

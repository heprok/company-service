package com.briolink.companyservice.common.mapper

import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.companyservice.common.domain.v2_0.CompanyType
import com.briolink.companyservice.common.domain.v2_0.InvestmentsData
import com.briolink.companyservice.common.domain.v2_0.MetricsData
import com.briolink.companyservice.common.domain.v2_0.PreferencesData
import com.briolink.companyservice.common.domain.v2_0.PrimaryContactData
import com.briolink.companyservice.common.domain.v2_0.RealAssetItemData
import com.briolink.companyservice.common.domain.v2_0.SICData
import com.briolink.companyservice.common.domain.v2_0.ServiceProviderFundData
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import java.util.UUID

fun CompanyWriteEntity.toDomainV1() = com.briolink.companyservice.common.domain.v1_0.Company(
    id = id!!,
    name = name,
    slug = slug,
    website = websiteUrl,
    logo = logo,
    description = description,
    shortDescription = shortDescription,
    isTypePublic = false,
    locationId = getLocationId(),
    facebook = facebook,
    twitter = twitter,
    occupation = null,
    industry = primaryIndustryId?.let { Industry(UUID.randomUUID(), it) },
    createdBy = createdBy ?: UUID.randomUUID(),
    keywords = ArrayList(keywordIds.map { com.briolink.companyservice.common.domain.v1_0.Company.Keyword(UUID.randomUUID(), it) })
)

fun CompanyWriteEntity.toDomainV2() = com.briolink.companyservice.common.domain.v2_0.Company(
    id = id!!,
    parentCompanyId = parentCompanyId,
    pbId = pbId,
    parentCompanyPbId = parentCompanyPbId,
    primaryCompanyType = CompanyType.valueOf(primaryCompanyType.name),
    companyTypes = ArrayList(companyTypes.map { CompanyType.valueOf(it.name) }),
    name = name,
    familiarName = familiarName,
    slug = slug,
    website = websiteUrl,
    logo = logo,
    description = description,
    shortDescription = shortDescription,
    yearFounded = yearFounded,
    facebook = facebook,
    twitter = twitter,
    employees = employees,
    locationId = getLocationId(),
    investorInfo = investorData?.toDomainV2(),
    startupInfo = startupData?.toDomainV2(),
    servicesProviderData = serviceProviderData?.toDomainV2(),
    createdBy = createdBy,
    created = created,
    changed = changed,
    keywordTagIds = ArrayList(keywordIds),
    verticalTagIds = ArrayList(verticalIds),
    primaryIndustryTagId = primaryIndustryId,
    otherIndustryTagIds = ArrayList(industryIds),

)

fun CompanyWriteEntity.InvestorData.toDomainV2() =
    com.briolink.companyservice.common.domain.v2_0.InvestorInfo(
        financingStatusTagId = financingStatusTagId,
        primaryInvestorTypeTagId = primaryInvestorTypeTagId,
        otherInvestorTypeTagIds = ArrayList(otherInvestorTypeTagIds),
        investorStatusTagId = investorStatusTagId,
        sic = sic?.let {
            SICData(
                tagsIds = ArrayList(it.tagIds),
                codes = ArrayList(it.codes),
            )
        },
        aum = aum,
        dryPowder = dryPowder,
        investmentProfessionals = investmentProfessionals,
        exits = exits,
        activeInvestedCompanies = ArrayList(activeInvestedCompanies),
        activeInvestedCompaniesCount = activeInvestedCompaniesCount,
        preferences = preferences?.let {
            PreferencesData(
                verticalTagIds = ArrayList(it.verticalTagIds),
                industryTagIds = ArrayList(it.industryTagIds),
                regions = ArrayList(it.regions),
                realAssets = ArrayList(it.realAssets.map { asset -> RealAssetItemData(asset.classTagId, ArrayList(asset.itemTagIds)) }),
                investmentTypeTagIds = ArrayList(it.investmentTypeTagIds),
                investmentHorizon = it.investmentHorizon,
                otherTagIds = ArrayList(it.otherTagIds),
                investmentAmount = it.investmentAmount,
                dealSize = it.dealSize,
                companyValuation = it.companyValuation,
                ebitda = it.ebitda,
                ebit = it.ebit,
                revenue = it.revenue,
            )
        },
        investments = investments?.let {
            InvestmentsData(
                total = it.total,
                last6Months = it.last6Months,
                last12Months = it.last12Months,
                last2Years = it.last2Years,
                last5Years = it.last5Years
            )
        },
        metrics = metrics?.let {
            MetricsData(
                medianQuartile = it.medianQuartile,
                totalNumberOfFunds = it.totalNumberOfFunds,
                topQuartileNumberOfFunds = it.topQuartileNumberOfFunds,
                upperMidQuartileNumberOfFunds = it.upperMidQuartileNumberOfFunds,
                midQuartileNumberOfFunds = it.midQuartileNumberOfFunds,
                bottomQuartileNumberOfFunds = it.bottomQuartileNumberOfFunds
            )
        }

    )

fun CompanyWriteEntity.StartupData.toDomainV2() = com.briolink.companyservice.common.domain.v2_0.StartupInfo(
    totalPatentDocs = totalPatentDocs,
    revenue = revenue,
    totalRaised = totalRaised,
    lastFinancing = lastFinancing,
    lastFinancingStatusTagId = lastFinancingStatusTagId,
    universeTagIds = ArrayList(universeTagIds),
    cpcCodesTagIds = ArrayList(cpcCodeTagIds),
    investors = ArrayList(investors),
    servicesProviders = ArrayList(servicesProviders),
    comparables = ArrayList(comparables),
    otherComparables = ArrayList(otherComparables),
    otherServicesProviders = ArrayList(otherServicesProviders),
    potentialInvestors = ArrayList(potentialInvestors),
)

fun CompanyWriteEntity.ServiceProviderData.toDomainV2() = com.briolink.companyservice.common.domain.v2_0.ServicesProviderInfo(
    primaryTypeTagId = primaryTypeTagId,
    otherTypeTagIds = ArrayList(otherTypeTagIds),
    serviced = serviced?.toDomainV2(),
    primaryContact = primaryContact?.let {
        PrimaryContactData(
            name = it.name,
            title = it.title,
            email = it.email,
            phone = it.phone,
            pbId = it.pbId,
            personId = it.personId,
            personSlug = it.personSlug
        )
    },
    lastDeal = lastDeal?.toDomainV2(),
    lastClosedFund = lastClosedFund?.let { fund ->
        ServiceProviderFundData(fund.name, ArrayList(fund.leadPartners.map { it.toDomainV2() }))
    }
)

fun CompanyWriteEntity.ServiceProviderServiced.toDomainV2() = com.briolink.companyservice.common.domain.v2_0.ServiceProviderServicedData(
    total = total,
    companies = companies,
    deals = deals,
    investors = investors,
    funds = funds
)

fun CompanyWriteEntity.ServiceProviderDealLeadPartner.toDomainV2() = com.briolink.companyservice.common.domain.v2_0.DealLeadPartnerData(
    pbId = pbId,
    name = name
)

fun CompanyWriteEntity.ServiceProviderDeal.toDomainV2() = com.briolink.companyservice.common.domain.v2_0.ServiceProviderDealData(
    date = date,
    size = size,
    valuation = valuation,
    valuationStatusTagId = valuationStatusTagId,
    typeTagId = typeTagId,
    classTagId = classTagId,
    leadPartners = ArrayList(leadPartners.map { it.toDomainV2() })
)

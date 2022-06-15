package com.briolink.companyservice.updater.handler.company

import com.briolink.companyservice.common.domain.v2_0.Company
import com.briolink.companyservice.common.enumeration.CompanyTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.entity.company.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.company.Investments
import com.briolink.companyservice.common.jpa.read.entity.company.InvestorData
import com.briolink.companyservice.common.jpa.read.entity.company.Metrics
import com.briolink.companyservice.common.jpa.read.entity.company.Preferences
import com.briolink.companyservice.common.jpa.read.entity.company.RealAssetItem
import com.briolink.companyservice.common.jpa.read.entity.company.SIC
import com.briolink.companyservice.common.jpa.read.entity.company.SPContact
import com.briolink.companyservice.common.jpa.read.entity.company.SPData
import com.briolink.companyservice.common.jpa.read.entity.company.SPDeal
import com.briolink.companyservice.common.jpa.read.entity.company.SPDealLeadPartner
import com.briolink.companyservice.common.jpa.read.entity.company.SPFund
import com.briolink.companyservice.common.jpa.read.entity.company.SPServiced
import com.briolink.companyservice.common.jpa.read.entity.company.StartupData
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.mapper.toEnum
import com.briolink.lib.common.type.basic.ValueWithCount
import com.briolink.lib.dictionary.service.DictionaryService
import com.briolink.lib.location.model.LocationMinInfo
import com.briolink.lib.location.service.LocationService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Year
import java.util.UUID

@Transactional
@Service
class CompanyHandlerService(
    private val companyReadRepository: CompanyReadRepository,
    private val locationService: LocationService,
    private val dictionaryService: DictionaryService,
    private val userReadRepository: UserReadRepository,
) {

    fun createOrUpdate(entityPrevCompany: CompanyReadEntity? = null, domain: Company): CompanyReadEntity {
        val company =
            entityPrevCompany ?: CompanyReadEntity(domain.id, domain.slug, domain.name, domain.primaryCompanyType.toEnum())

        val companies = companyReadRepository.findByIdIsIn(domain.companyIds.toList()).associateBy { it.id }
        val tags = dictionaryService.getTags(domain.tagIds).associateBy { it.id }

        company.apply {
            slug = domain.slug
            name = domain.name
            primaryCompanyType = domain.primaryCompanyType.toEnum()
            companyTypes = domain.companyTypes.map { it.toEnum() }
            createdBy = domain.createdBy
            data = CompanyReadEntity.Data(
                info = CompanyReadEntity.BaseCompany(
                    id = id,
                    primaryType = primaryCompanyType,
                    pbId = domain.pbId,
                    slug = slug,
                    name = name,
                    logo = domain.logo
                ),
                parentCompany = domain.parentCompanyId?.let { companies[it]?.data?.info ?: getEmptyInfoCompany(it) },
                website = domain.website,
                familiarName = domain.familiarName,
                description = domain.description,
                shortDescription = domain.shortDescription,
                location = domain.locationId?.let { locationService.getLocationInfo(it, LocationMinInfo::class.java) },
                keywords = domain.keywordTagIds.map { tags[it]!! }.toMutableSet(),
                verticals = domain.verticalTagIds.map { tags[it]!! }.toMutableSet(),
                primaryIndustry = domain.primaryIndustryTagId?.let { tags[it]!! },
                otherIndustries = domain.otherIndustryTagIds.map { tags[it]!! }.toMutableSet(),
                yearFounded = domain.yearFounded?.let { Year.of(it) },
                facebook = domain.facebook,
                twitter = domain.twitter,
                employees = domain.employees,

            )

            startupData = domain.startupInfo?.let { info ->
                StartupData(
                    totalPatentDocs = info.totalPatentDocs,
                    revenue = info.revenue,
                    totalRaised = info.totalRaised,
                    lastFinancing = info.lastFinancing,
                    lastFinancingStatus = info.lastFinancingStatusTagId?.let { tags[it]!! },
                    universes = info.universeTagIds.map { tags[it]!! }.toMutableSet(),
                    cpcCodes = info.cpcCodesTagIds.map { tags[it]!! }.toMutableSet(),
                    investors = info.investors.map { companies[it]?.data?.info ?: getEmptyInfoCompany(it) }.toMutableSet(),
                    potentialInvestors = info.potentialInvestors
                        .map { ValueWithCount(companies[it.value]?.data?.info ?: getEmptyInfoCompany(it.value), it.count) }
                        .toMutableSet(),
                    servicesProviders = info.servicesProviders.map { companies[it]?.data?.info ?: getEmptyInfoCompany(it) }.toMutableSet(),
                    otherServicesProviders = info.otherServicesProviders.toMutableSet(),
                    comparables = info.comparables
                        .map { ValueWithCount(companies[it.value]?.data?.info ?: getEmptyInfoCompany(it.value), it.count) }
                        .toMutableSet(),
                    otherComparables = info.otherComparables.map { ValueWithCount(it.value, it.count) }.toMutableSet(),
                )
            }

            investorData = domain.investorInfo?.let { info ->
                InvestorData(
                    financingStatus = info.financingStatusTagId?.let { tags[it]!! },
                    primaryInvestorType = info.primaryInvestorTypeTagId?.let { tags[it]!! },
                    otherInvestorTypes = info.otherInvestorTypeTagIds.map { tags[it]!! }.toMutableSet(),
                    investorStatus = info.investorStatusTagId?.let { tags[it]!! },
                    sic = info.sic?.let {
                        SIC(
                            tags = it.tagsIds.map { tags[it]!! }.toMutableSet(),
                            codes = it.codes.toMutableSet()
                        )
                    },
                    aum = info.aum,
                    dryPowder = info.dryPowder,
                    investmentProfessionals = info.investmentProfessionals,
                    exits = info.exits,
                    activeInvestedCompanies = info.activeInvestedCompanies
                        .map { companies[it]?.data?.info ?: getEmptyInfoCompany(it) }
                        .toMutableSet(),
                    activeInvestedCompaniesCount = info.activeInvestedCompaniesCount,
                    preferences = info.preferences?.let {
                        Preferences(
                            verticals = it.verticalTagIds.map { tags[it]!! }.toMutableSet(),
                            industries = it.industryTagIds.map { tags[it]!! }.toMutableSet(),
                            regions = it.regions.map { locationService.getLocationInfo(it, LocationMinInfo::class.java)!! }.toMutableSet(),
                            realAssets = it.realAssets.map {
                                RealAssetItem(
                                    `class` = tags[it.classTagId]!!,
                                    item = it.itemTagIds.map { tags[it]!! }.toMutableSet()
                                )
                            }.toMutableSet(),
                            investmentTypes = it.investmentTypeTagIds.map { tags[it]!! }.toMutableSet(),
                            investmentHorizon = it.investmentHorizon,
                            others = it.otherTagIds.map { tags[it]!! }.toMutableSet(),
                            investmentAmount = it.investmentAmount,
                            dealSize = it.dealSize,
                            companyValuation = it.companyValuation,
                            ebitda = it.ebitda,
                            ebit = it.ebit,
                            revenue = it.revenue
                        )
                    },
                    investments = info.investments?.let {
                        Investments(
                            total = it.total,
                            last6Months = it.last6Months,
                            last12Months = it.last12Months,
                            last2Years = it.last2Years,
                            last5Years = it.last5Years
                        )
                    },
                    metrics = info.metrics?.let {
                        Metrics(
                            medianQuartile = it.medianQuartile,
                            totalNumberOfFunds = it.totalNumberOfFunds,
                            topQuartileNumberOfFunds = it.topQuartileNumberOfFunds,
                            upperMidQuartileNumberOfFunds = it.upperMidQuartileNumberOfFunds,
                            midQuartileNumberOfFunds = it.midQuartileNumberOfFunds,
                            bottomQuartileNumberOfFunds = it.bottomQuartileNumberOfFunds
                        )
                    }

                )
            }
            spData = domain.servicesProviderData?.let { info ->
                SPData(
                    primaryType = info.primaryTypeTagId?.let { tags[it]!! },
                    otherTypes = info.otherTypeTagIds.map { tags[it]!! }.toMutableSet(),
                    serviced = info.serviced?.let {
                        SPServiced(
                            companies = it.companies,
                            deals = it.deals,
                            investors = it.investors,
                            funds = it.funds
                        )
                    },
                    primaryContact = info.primaryContact?.let { contact ->
                        val user: UserReadEntity? = contact.personId?.let { userReadRepository.findByIdOrNull(it) }

                        SPContact(
                            name = contact.name,
                            title = contact.title,
                            email = contact.email,
                            phone = contact.phone,
                            pbId = contact.pbId,
                            personId = contact.personId,
                            user = user?.let {
                                UserReadEntity.BaseUser(
                                    id = it.id,
                                    pbId = contact.pbId,
                                    firstName = it.data.firstName,
                                    lastName = it.data.lastName,
                                    image = it.data.image,
                                    slug = it.data.slug
                                )
                            }
                        )
                    },
                    lastDeal = info.lastDeal?.let { spDeal ->
                        SPDeal(
                            date = spDeal.date,
                            size = spDeal.size,
                            valuation = spDeal.valuation,
                            valuationStatus = spDeal.valuationStatusTagId?.let { tags[it]!! },
                            type = spDeal.typeTagId?.let { tags[it]!! },
                            `class` = spDeal.classTagId?.let { tags[it]!! },
                            leadPartners = spDeal.leadPartners.map {
                                SPDealLeadPartner(pbId = it.pbId, name = it.name)
                            }.toMutableSet()

                        )
                    },
                    lastClosedFund = info.lastClosedFund?.let {
                        SPFund(
                            name = it.name,
                            leadPartners = it.leadPartners.map { SPDealLeadPartner(it.pbId, it.name) }.toMutableSet()
                        )
                    }

                )
            }

            return companyReadRepository.save(this)
        }
    }

    fun findById(companyId: UUID): CompanyReadEntity? = companyReadRepository.findByIdOrNull(companyId)

    private fun getEmptyInfoCompany(id: UUID) = CompanyReadEntity.BaseCompany(id, CompanyTypeEnum.Startup, null, "", "", null)
}

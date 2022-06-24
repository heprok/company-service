package com.briolink.companyservice.api.service.company

import com.briolink.companyservice.api.service.company.dto.CreatedCompanyDto
import com.briolink.companyservice.common.jpa.read.entity.company.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import com.briolink.companyservice.common.mapper.toDomainV1
import com.briolink.companyservice.common.mapper.toDomainV2
import com.briolink.lib.common.utils.BlS3Utils
import com.briolink.lib.common.utils.StringUtils
import com.briolink.lib.dictionary.dto.TagCreateRequest
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.dictionary.service.DictionaryService
import com.briolink.lib.event.publisher.EventPublisher
import com.briolink.lib.location.model.LocationFullInfo
import com.briolink.lib.location.service.LocationService
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncUtil
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.model.PeriodDateTime
import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.HeaderColumnNameMappingStrategy
import liquibase.pro.packaged.it
import mu.KLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedInputStream
import java.net.URL
import java.util.Optional
import java.util.UUID
import javax.persistence.EntityNotFoundException
import javax.validation.Valid

@Service
@Transactional
@Validated
class CompanyService(
    private val companyReadRepository: CompanyReadRepository,
    private val companyWriteRepository: CompanyWriteRepository,
    private val locationService: LocationService,
    private val dictionaryService: DictionaryService,
    val eventPublisher: EventPublisher,
    private val s3Utils: BlS3Utils,
) {
    companion object : KLogging()

    private val PATH_LOGO_PROFILE_COMPANY = "uploads/company/profile-image"

    fun createCompanies(@Valid listDto: List<@Valid CreatedCompanyDto>): List<CompanyWriteEntity> {
        return listDto.map {
            createCompany(it)
        }
    }

    fun createCompany(@Valid dto: CreatedCompanyDto): CompanyWriteEntity {
        val emptyCompany = dto.website?.let { getByWebsite(StringUtils.prepareUrl(it)) }
            ?: CompanyWriteEntity(dto.name, dto.slug ?: "", dto.primaryCompanyType)

        val company = if (dto.id == null) emptyCompany else companyWriteRepository.findById(dto.id).orElse(emptyCompany)
        val location = dto.locationId?.let { locationService.getLocationInfo(it, LocationFullInfo::class.java) }

        val dtoTags = dto.getAllTags()
        val dictionaryTags = createTagsIfNotExists(dtoTags)

        if (!dictionaryTags.keys.containsAll(dtoTags.map { it.id })) {
            logger.error("Dictionary tags:  ${dictionaryTags.values}")
            logger.error("Dto All tags: $dtoTags")
            throw RuntimeException("Tags not found")
        }

        company.apply {
            pbId = dto.pbId
            parentCompanyId = dto.parentCompanyId
            parentCompanyPbId = dto.parentCompanyPbId
            companyTypes = dto.otherCompanyTypes ?: mutableSetOf()
            websiteUrl = dto.website
            familiarName = dto.familiarName
            logo = dto.logo
            description = dto.description
            shortDescription = dto.shortDescription
            hqCountryId = location?.country?.id
            hqStateId = location?.state?.id
            hqCityId = location?.city?.id

            keywordIds.addAll(dto.keywords.map { it.id })
            verticalIds.addAll(dto.verticals.map { it.id })
            primaryIndustryId = dto.primaryIndustry?.id
            industryIds.addAll(dto.industries.map { it.id })

            yearFounded = dto.yearFounded?.value
            facebook = dto.facebook
            twitter = dto.twitter
            employees = dto.employees
            createdBy = dto.createBy

            startupData = dto.startupInfoDto?.let { infoDto ->
                CompanyWriteEntity.StartupData(
                    totalPatentDocs = infoDto.totalPatentDocs,
                    revenue = infoDto.revenue,
                    totalRaised = infoDto.totalRaised,
                    lastFinancing = infoDto.lastFinancing,
                    lastFinancingStatusTagId = infoDto.lastFinancingStatus?.id,
                    universeTagIds = infoDto.universe.map { it.id }.toMutableSet(),
                    cpcCodeTagIds = infoDto.cpcCodes.map { it.id }.toMutableSet(),
                    investors = infoDto.investors,
                    potentialInvestors = infoDto.potentialInvestors,
                    servicesProviders = infoDto.servicesProviders,
                    otherServicesProviders = infoDto.otherServicesProviders,
                    comparables = infoDto.comparables,
                    otherComparables = infoDto.otherComparables,
                )
            }

            investorData = dto.investorInfoDto?.let { infoDto ->
                CompanyWriteEntity.InvestorData(
                    financingStatusTagId = infoDto.financingStatus?.id,
                    primaryInvestorTypeTagId = infoDto.primaryInvestorType?.id,
                    otherInvestorTypeTagIds = infoDto.otherInvestorTypes.map { it.id }.toMutableSet(),
                    investorStatusTagId = infoDto.investorStatus?.id,
                    sic = infoDto.sic?.let { sicDto ->
                        CompanyWriteEntity.SIC(
                            tagIds = sicDto.tags.map { it.id }.toMutableSet(),
                            codes = sicDto.codes

                        )
                    },
                    aum = infoDto.aum,
                    dryPowder = infoDto.dryPowder,
                    investmentProfessionals = infoDto.investmentProfessionals,
                    exits = infoDto.exits,
                    activeInvestedCompanies = infoDto.activeInvestedCompanies,
                    activeInvestedCompaniesCount = infoDto.activeInvestedCompaniesCount,
                    preferences = infoDto.preferences?.let { pDto ->
                        CompanyWriteEntity.Preferences(
                            verticalTagIds = pDto.verticals.map { it.id }.toMutableSet(),
                            industryTagIds = pDto.industries.map { it.id }.toMutableSet(),
                            regions = pDto.regions,
                            realAssets = pDto.realAssets.map {
                                CompanyWriteEntity.RealAssetItem(
                                    classTagId = it.`class`.id,
                                    itemTagIds = it.items.map { it.id }.toMutableSet()
                                )
                            }.toMutableSet(),
                            investmentTypeTagIds = pDto.investmentTypes.map { it.id }.toMutableSet(),
                            investmentHorizon = pDto.investmentHorizon,
                            otherTagIds = pDto.other.map { it.id }.toMutableSet(),
                            investmentAmount = pDto.investmentAmount,
                            dealSize = pDto.dealSize,
                            companyValuation = pDto.companyValuation,
                            ebitda = pDto.ebitda,
                            ebit = pDto.ebit,
                            revenue = pDto.revenue
                        )
                    },
                    investments = infoDto.investments?.let {
                        CompanyWriteEntity.Investments(
                            total = it.total,
                            last6Months = it.last6Months,
                            last12Months = it.last12Months,
                            last2Years = it.last2Years,
                            last5Years = it.last5Years
                        )
                    },
                    metrics = infoDto.metrics?.let {
                        CompanyWriteEntity.Metrics(
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

            serviceProviderData = dto.serviceProviderInfo?.let { infoDto ->
                CompanyWriteEntity.ServiceProviderData(
                    primaryTypeTagId = infoDto.primaryType?.id,
                    otherTypeTagIds = infoDto.otherTypes.map { it.id }.toMutableSet(),
                    lastClosedFund = infoDto.lastClosedFund?.let {
                        CompanyWriteEntity.ServiceProviderFund(
                            name = it.name,
                            leadPartners = it.leadPartners.map { spLp ->
                                CompanyWriteEntity.ServiceProviderDealLeadPartner(spLp.pbId, spLp.name)
                            }.toMutableSet(),
                        )
                    },

                    serviced = infoDto.serviced?.let {
                        CompanyWriteEntity.ServiceProviderServiced(
                            companies = it.companies,
                            deals = it.deals,
                            investors = it.investors,
                            funds = it.funds
                        )
                    },
                    primaryContact = infoDto.primaryContact?.let {
                        CompanyWriteEntity.ServiceProviderContact(
                            name = it.name,
                            title = it.title,
                            email = it.email,
                            phone = it.phone,
                            pbId = it.pbId,
                            personId = it.personId,
                            personSlug = it.personSlug

                        )
                    },
                    lastDeal = infoDto.lastDeal?.let { spD ->
                        CompanyWriteEntity.ServiceProviderDeal(
                            date = spD.date,
                            size = spD.size,
                            valuation = spD.valuation,
                            valuationStatusTagId = spD.valuationStatus?.id,
                            typeTagId = spD.type?.id,
                            classTagId = spD.`class`?.id,
                            leadPartners = spD.leadPartners.map {
                                CompanyWriteEntity.ServiceProviderDealLeadPartner(
                                    pbId = it.pbId,
                                    name = it.name
                                )
                            }.toMutableSet(),

                        )
                    },

                )
            }

            return saveAndSendEvent(this, true)
        }
    }

    private fun createTagsIfNotExists(tags: Set<Tag>): MutableMap<String, Tag> {
        val mapTags = mutableMapOf<String, Tag>()

        tags.forEach { tag ->
            dictionaryService.getTagIfNotExistsCreate(TagCreateRequest(tag.id, tag.name, tag.type, tag.path)).also {
                mapTags[it.id] = it
            }
        }

        return mapTags
    }

    @Async
    @Deprecated("Remove this method")
    fun importLogoFromCSV(keyCsvOnS3: String): List<String> {
        class CompanyImport {
            @CsvBindByName
            lateinit var name: String

            @CsvBindByName
            var website: String? = null

            @CsvBindByName
            val logotype: String? = null
        }
        logger.info("Import starting...")
        val csvStream = s3Utils.getUrl(keyCsvOnS3).openStream()
        val streamBufferCompanies = BufferedInputStream(csvStream)
        val companyListNotUploadImage = mutableListOf<String>()
        val strategy: HeaderColumnNameMappingStrategy<CompanyImport> = HeaderColumnNameMappingStrategy()
        strategy.type = CompanyImport::class.java

        val csvToBean: CsvToBean<CompanyImport> =
            CsvToBeanBuilder<CompanyImport>(streamBufferCompanies.bufferedReader()).withMappingStrategy(strategy)
                .withIgnoreLeadingWhiteSpace(true).build()

        csvToBean.parse().forEach { company ->
            var isUpdateCompany = false
            company.name = StringUtils.trimAllSpaces(company.name)
            val companyWriteEntity = getByWebsite(StringUtils.prepareUrl(company.website))
            if (companyWriteEntity == null) {
                logger.error(company.name + " company not exist")
                return@forEach
            }
            if (company.logotype != null && companyWriteEntity.logo != null) {
                val logoUrl = s3Utils.uploadImage(PATH_LOGO_PROFILE_COMPANY, URL(company.logotype))
                if (logoUrl == null) {
                    companyListNotUploadImage.add(company.name)
                    logger.error(company.name + " not download image " + company.logotype)
                } else {
                    companyWriteEntity.logo = logoUrl
                    isUpdateCompany = true
                }
            }

            if (company.website != null && companyWriteEntity.websiteUrl != StringUtils.prepareUrl(company.website)) {
                companyWriteEntity.websiteUrl = StringUtils.prepareUrl(company.website)
                isUpdateCompany = true
            }

            if (isUpdateCompany) saveAndSendEvent(companyWriteEntity)
        }
        csvStream.close()
        companyListNotUploadImage.forEach {
            logger.info(it)
        }
        return companyListNotUploadImage
    }

    fun isExistWebsite(website: URL): Boolean {
        return companyWriteRepository.existsByWebsite(StringUtils.prepareUrl(website)!!.host)
    }

    private fun saveAndSendEvent(company: CompanyWriteEntity, isNew: Boolean = false): CompanyWriteEntity {
        return companyWriteRepository.save(company).also {
            if (isNew) {
                eventPublisher.publishAsync(com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent(it.toDomainV1()))
                eventPublisher.publishAsync(com.briolink.companyservice.common.event.v2_0.CompanyCreatedEvent(it.toDomainV2()))
            } else {
                eventPublisher.publishAsync(com.briolink.companyservice.common.event.v1_0.CompanyUpdatedEvent(it.toDomainV1()))
                eventPublisher.publishAsync(com.briolink.companyservice.common.event.v2_0.CompanyUpdatedEvent(it.toDomainV2()))
            }
        }
    }

    fun deleteCompany(id: UUID) = companyWriteRepository.deleteById(id)
    fun getCompanyBySlug(slug: String): CompanyReadEntity? = companyReadRepository.findBySlug(slug)
    fun findById(id: UUID): Optional<CompanyWriteEntity> = companyWriteRepository.findById(id)
    fun uploadCompanyProfileImage(id: UUID, image: MultipartFile?): URL? {
        val company = findById(id).orElseThrow { throw EntityNotFoundException("company with $id not found") }
        val imageUrl: URL? = if (image != null) s3Utils.uploadImage(PATH_LOGO_PROFILE_COMPANY, image) else null
        company.logo = imageUrl
        saveAndSendEvent(company)
        return imageUrl
    }

    fun getByWebsite(website: URL?): CompanyWriteEntity? =
        if (website != null) companyWriteRepository.getByWebsiteHost(website.host) else null

    private fun publishCompanySyncEvent(
        syncId: Int,
        objectIndex: Long,
        totalObjects: Long,
        entity: CompanyWriteEntity?
    ) {
        eventPublisher.publishAsync(
            com.briolink.companyservice.common.event.v1_0.CompanySyncEvent(
                SyncData(
                    objectIndex = objectIndex,
                    totalObjects = totalObjects,
                    objectSync = entity?.toDomainV1(),
                    syncId = syncId,
                    service = ServiceEnum.Company,
                ),
            ),
        )
        eventPublisher.publishAsync(
            com.briolink.companyservice.common.event.v2_0.CompanySyncEvent(
                SyncData(
                    objectIndex = objectIndex,
                    totalObjects = totalObjects,
                    objectSync = entity?.toDomainV2(),
                    syncId = syncId,
                    service = ServiceEnum.Company,
                ),
            ),
        )
    }

    fun publishSyncEvent(syncId: Int, period: PeriodDateTime? = null) {
        SyncUtil.publishSyncEvent(period, companyWriteRepository) { indexElement, totalElements, entity ->
            publishCompanySyncEvent(
                syncId, indexElement, totalElements,
                entity as CompanyWriteEntity?,
            )
        }
    }

    fun existsCompanyName(name: String): Boolean = companyWriteRepository.existsByName(name)
}

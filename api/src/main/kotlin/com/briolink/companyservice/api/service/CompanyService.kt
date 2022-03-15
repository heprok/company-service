package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.event.v1_0.CompanySyncEvent
import com.briolink.companyservice.common.event.v1_0.CompanyUpdatedEvent
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import com.briolink.companyservice.common.util.StringUtil
import com.briolink.event.publisher.EventPublisher
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRoleEnum
import com.briolink.lib.permission.service.PermissionService
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncUtil
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.model.PeriodDateTime
import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.HeaderColumnNameMappingStrategy
import mu.KLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedInputStream
import java.net.URL
import java.util.Optional
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class CompanyService(
    private val companyReadRepository: CompanyReadRepository,
    private val companyWriteRepository: CompanyWriteRepository,
    private val industryService: IndustryService,
    val eventPublisher: EventPublisher,
    private val permissionService: PermissionService,
    private val awsS3Service: AwsS3Service,
) {
    companion object : KLogging()

    private val PATH_LOGO_PROFILE_COMPANY = "uploads/company/profile-image"
    fun createCompany(createCompany: CompanyWriteEntity): Company {
        return companyWriteRepository.save(createCompany).let {
            eventPublisher.publish(CompanyCreatedEvent(it.toDomain()))
            permissionService.createPermissionRole(
                userId = it.createdBy,
                accessObjectType = AccessObjectTypeEnum.Company,
                accessObjectId = it.id!!,
                permissionRole = PermissionRoleEnum.Owner,
            )
            it.toDomain()
        }
    }

    @Async
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
        val csvStream = awsS3Service.getUrl(keyCsvOnS3).openStream()
        val streamBufferCompanies = BufferedInputStream(csvStream)
        val companyListNotUploadImage = mutableListOf<String>()
        val strategy: HeaderColumnNameMappingStrategy<CompanyImport> = HeaderColumnNameMappingStrategy()
        strategy.type = CompanyImport::class.java

        val csvToBean: CsvToBean<CompanyImport> =
            CsvToBeanBuilder<CompanyImport>(streamBufferCompanies.bufferedReader())
                .withMappingStrategy(strategy)
                .withIgnoreLeadingWhiteSpace(true)
                .build()

        csvToBean.parse().forEach { company ->
            var isUpdateCompany = false
            company.name = StringUtil.trimAllSpaces(company.name)
            val companyWriteEntity = getByNameAndWebsite(company.name, StringUtil.prepareUrl(company.website))
            if (companyWriteEntity == null) {
                logger.error(company.name + " company not exist")
                return@forEach
            }
            if (company.logotype != null && companyWriteEntity.logo != null) {
                val logoUrl = awsS3Service.uploadImage(PATH_LOGO_PROFILE_COMPANY, URL(company.logotype))
                if (logoUrl == null) {
                    companyListNotUploadImage.add(company.name)
                    logger.error(company.name + " not download image " + company.logotype)
                } else {
                    companyWriteEntity.logo = logoUrl
                    isUpdateCompany = true
                }
            }

            if (company.website != null && companyWriteEntity.websiteUrl != StringUtil.prepareUrl(company.website)) {
                companyWriteEntity.websiteUrl = StringUtil.prepareUrl(company.website)
                isUpdateCompany = true
            }

            if (isUpdateCompany)
                updateCompany(companyWriteEntity)
        }
        csvStream.close()
        companyListNotUploadImage.forEach {
            logger.info(it)
        }
        return companyListNotUploadImage
    }

    fun createCompany(
        name: String,
        website: URL?,
        imageUrl: URL?,
        description: String?,
        industryName: String?,
        createdBy: UUID
    ): CompanyWriteEntity {
        val companyWrite = getByNameAndWebsite(name, website)
        val industryWrite = industryName?.let { industryService.create(industryName) }
        val s3ImageUrl = if (imageUrl != null)
            awsS3Service.uploadImage(PATH_LOGO_PROFILE_COMPANY, imageUrl) else null
        if (companyWrite != null && s3ImageUrl != null && companyWrite.logo == null) {
            companyWrite.logo = s3ImageUrl
            updateCompany(companyWrite)
        }
        return companyWrite ?: CompanyWriteEntity(name = name, createdBy = createdBy).apply {
            websiteUrl = website
            logo = s3ImageUrl
            this.description = description
            industry = industryWrite
            createCompany(this)
        }
    }

    fun isExistWebsite(website: URL): Boolean {
        return companyWriteRepository.existsByWebsite(StringUtil.prepareUrl(website)!!.host)
    }

    fun updateCompany(company: CompanyWriteEntity): Company {
        return companyWriteRepository.save(company).let {
            eventPublisher.publishAsync(CompanyUpdatedEvent(it.toDomain()))
            it.toDomain()
        }
    }

    fun deleteCompany(id: UUID) = companyWriteRepository.deleteById(id)
    fun getCompanyBySlug(slug: String): CompanyReadEntity? = companyReadRepository.findBySlug(slug)
    fun findById(id: UUID): Optional<CompanyWriteEntity> = companyWriteRepository.findById(id)
    fun uploadCompanyProfileImage(id: UUID, image: MultipartFile?): URL? {
        val company = findById(id).orElseThrow { throw EntityNotFoundException("company with $id not found") }
        val imageUrl: URL? =
            if (image != null) awsS3Service.uploadImage(PATH_LOGO_PROFILE_COMPANY, image) else null
        company.logo = imageUrl
        updateCompany(company)
        return imageUrl
    }

    fun getByNameAndWebsite(name: String, website: URL?): CompanyWriteEntity? =
        companyWriteRepository.getByNameIgnoreCaseAndWebsiteIgnoreCase(name, website?.host)
            ?: if (website != null) companyWriteRepository.getByWebsite(website.host) else null

    private fun publishCompanySyncEvent(
        syncId: Int,
        objectIndex: Long,
        totalObjects: Long,
        entity: CompanyWriteEntity?
    ) {
        eventPublisher.publishAsync(
            CompanySyncEvent(
                SyncData(
                    objectIndex = objectIndex,
                    totalObjects = totalObjects,
                    objectSync = entity?.toDomain(),
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

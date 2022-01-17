package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.event.v1_0.CompanySyncEvent
import com.briolink.companyservice.common.event.v1_0.CompanyUpdatedEvent
import com.briolink.companyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumeration.UserPermissionRoleTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import com.briolink.event.publisher.EventPublisher
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
@Async
class CompanyService(
    private val companyReadRepository: CompanyReadRepository,
    private val companyWriteRepository: CompanyWriteRepository,
    private val industryService: IndustryService,
    val eventPublisher: EventPublisher,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    private val awsS3Service: AwsS3Service,
) {
    companion object : KLogging()

    private val PATH_LOGO_PROFILE_COMPANY = "uploads/company/profile-image"
    fun createCompany(createCompany: CompanyWriteEntity): Company {
        return companyWriteRepository.save(createCompany).let {
            eventPublisher.publish(CompanyCreatedEvent(it.toDomain()))
            it.toDomain()
        }
    }

    @Async
    fun importLogoFromCSV(keyCsvOnS3: String): List<String> {
        class CompanyImport {
            @CsvBindByName
            lateinit var name: String

            @CsvBindByName
            val website: String? = null

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
            val listCompany = companyWriteRepository.getAllByName(company.name)
            if (listCompany.isEmpty()) {
                logger.error(company.name + " company not exist")
                return@forEach
            }
            listCompany.forEach {
                if (company.logotype != null) {
                    val logoUrl = awsS3Service.uploadImage(PATH_LOGO_PROFILE_COMPANY, URL(company.logotype))
                    if (logoUrl == null) {
                        companyListNotUploadImage.add(company.name)
                        logger.error(company.name + " not download image " + company.logotype)
                    } else {
                        it.logo = logoUrl
                        isUpdateCompany = true
                    }
                }

                if (company.website != null) {
                    it.websiteUrl = URL("https://" + company.website)
                    isUpdateCompany = true
                }

                if (isUpdateCompany)
                    updateCompany(it)
            }
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
        val companyWrite =
            if (website != null) companyWriteRepository.getByWebsite(website.host.replace(Regex("^www."), "")) else null
        val industryWrite = industryName?.let { industryService.create(industryName) }
        val s3ImageUrl = if (imageUrl != null)
            awsS3Service.uploadImage(PATH_LOGO_PROFILE_COMPANY, imageUrl) else null

        return companyWrite ?: CompanyWriteEntity(name = name, createdBy = createdBy).apply {
            websiteUrl = website
            logo = s3ImageUrl
            this.description = description
            industry = industryWrite
            companyWriteRepository.save(this).also {
                eventPublisher.publish(CompanyCreatedEvent(it.toDomain()))
            }
        }
    }

    fun isExistWebsite(website: URL): Boolean {
        return companyWriteRepository.existsByWebsite(website.host.replace(Regex("^www."), ""))
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

    fun getPermission(companyId: UUID, userId: UUID): UserPermissionRoleTypeEnum? {
        return userPermissionRoleReadRepository.getUserPermissionRole(
            accessObjectUuid = companyId,
            accessObjectType = AccessObjectTypeEnum.Company.value,
            userId = userId,
        )?.role
    }

    fun setPermission(
        companyId: UUID,
        userId: UUID,
        roleType: UserPermissionRoleTypeEnum
    ): UserPermissionRoleReadEntity {
        (
            userPermissionRoleReadRepository.getUserPermissionRole(
                accessObjectUuid = companyId,
                userId = userId,
                accessObjectType = AccessObjectTypeEnum.CompanyService.value,
            ) ?: UserPermissionRoleReadEntity(accessObjectUuid = companyId, userId = userId)
            ).apply {
            role = roleType
            return userPermissionRoleReadRepository.save(this)
        }
    }

    fun publishSyncEvent() {
        companyWriteRepository.findAll().forEach {
            eventPublisher.publishAsync(CompanySyncEvent(it.toDomain()))
        }
    }

    fun existsCompanyName(name: String): Boolean = companyWriteRepository.existsByName(name)
}

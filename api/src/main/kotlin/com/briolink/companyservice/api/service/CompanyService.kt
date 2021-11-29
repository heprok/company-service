package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.event.v1_0.CompanyUpdatedEvent
import com.briolink.companyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumeration.UserPermissionRoleTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import com.briolink.companyservice.common.service.LocationService
import com.briolink.event.publisher.EventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.util.Optional
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class CompanyService(
    private val companyReadRepository: CompanyReadRepository,
    private val companyWriteRepository: CompanyWriteRepository,
    val eventPublisher: EventPublisher,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    private val awsS3Service: AwsS3Service,
    private val locationService: LocationService
) {
    fun createCompany(createCompany: CompanyWriteEntity): Company {
        return companyWriteRepository.save(createCompany).let {
            eventPublisher.publish(
                CompanyCreatedEvent(
                    it.toDomain().apply {
                        location = locationId?.let { locationService.getLocation(it)?.toString() }
                    },
                ),
            )
            it.toDomain()
        }
    }

    fun createCompany(name: String, website: URL?, createdBy: UUID): CompanyWriteEntity {
        val companyWrite = if (website == null)
            companyWriteRepository.getByName(name)
        else
            companyWriteRepository.getByNameOrWebsite(name, website.host)

        return companyWrite ?: CompanyWriteEntity(name = name, createdBy = createdBy).apply {
            websiteUrl = website
            companyWriteRepository.save(this).let {
                eventPublisher.publish(CompanyCreatedEvent(it.toDomain()))
            }
        }
    }

    fun isExistWebsite(website: URL): Boolean {
        return companyWriteRepository.existsByWebsite(website.host)
    }

    fun updateCompany(company: CompanyWriteEntity): Company {
        return companyWriteRepository.save(company).let {
            eventPublisher.publishAsync(CompanyUpdatedEvent(it.toDomain()))
            it.toDomain()
        }
    }

    fun deleteCompany(id: UUID) = companyWriteRepository.deleteById(id)
    fun getCompanyBySlug(slug: String): CompanyReadEntity = companyReadRepository.findBySlug(slug)
    fun findById(id: UUID): Optional<CompanyWriteEntity> = companyWriteRepository.findById(id)
    fun uploadCompanyProfileImage(id: UUID, image: MultipartFile?): URL? {
        val company = findById(id).orElseThrow { throw EntityNotFoundException("company with $id not found") }
        val imageUrl: URL? = if (image != null) awsS3Service.uploadImage("uploads/company/profile-image", image) else null
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

    fun setPermission(companyId: UUID, userId: UUID, roleType: UserPermissionRoleTypeEnum): UserPermissionRoleReadEntity {
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
}

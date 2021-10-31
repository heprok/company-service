package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.companyservice.common.domain.v1_0.Occupation
import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.event.v1_0.CompanyUpdatedEvent
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.event.publisher.EventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.util.*
import javax.persistence.EntityNotFoundException
import kotlin.collections.ArrayList

@Service
@Transactional
class CompanyService(
    private val companyReadRepository: CompanyReadRepository,
    private val companyWriteRepository: CompanyWriteRepository,
    val eventPublisher: EventPublisher,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    private val awsS3Service: AwsS3Service
) {
    fun createCompany(createCompany: CompanyWriteEntity): Company {
        val company = companyWriteRepository.save(createCompany)
        val companyDomain = Company(
                id = company.id!!,
                name = company.name,
                slug = company.slug,
                logo = company.logo,
                website = company.website,
                description = company.description,
                isTypePublic = company.isTypePublic,
                twitter = company.twitter,
                facebook = company.facebook,
                location = company.location,
                industry = company.industry?.let {
                    Industry(
                            id = it.id!!,
                            name = it.name,
                    )
                },
                occupation = company.occupation?.let {
                    Occupation(
                            id = it.id!!,
                            name = it.name,
                    )
                },
        ).apply {
            keywords!!.addAll(
                    company.keywords.let { it ->
                        it.map {
                            Company.Keyword(
                                    id = it.id!!,
                                    name = it.name,
                            )
                        }
                    }
            )
        }
        eventPublisher.publishAsync(
                CompanyCreatedEvent(companyDomain),
        )
        return companyDomain
    }

    fun isExistWebsite(website: String): Boolean {
        return companyWriteRepository.existsByWebsiteIsLike(website)
    }

    fun updateCompany(company: CompanyWriteEntity): Company {
        val companyWrite = companyWriteRepository.save(company)
        val domain = Company(
                id = companyWrite.id!!,
                name = companyWrite.name,
                website = companyWrite.website,
                description = companyWrite.description,
                slug = companyWrite.slug,
//                country = companyWrite.country,
//                state = companyWrite.state,
                logo = companyWrite.logo,
                isTypePublic = companyWrite.isTypePublic,
//                city = companyWrite.city,
                location = companyWrite.location,
                facebook = companyWrite.facebook,
                twitter = companyWrite.twitter,
                industry = companyWrite.industry?.let {
                    Industry(
                            it.id!!,
                            it.name,
                    )
                },
                occupation = company.occupation?.let {
                    Occupation(
                            it.id!!,
                            it.name,
                    )
                },
                keywords = ArrayList(
                        company.keywords.map {
                            Company.Keyword(
                                    it.id!!,
                                    it.name,
                            )
                        },
                ),
        )
        eventPublisher.publishAsync(CompanyUpdatedEvent(domain))
        return domain
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

    fun getPermission(companyId: UUID, userId: UUID): UserPermissionRoleReadEntity.RoleType? {
        return userPermissionRoleReadRepository.findByAccessObjectUuidAndAccessObjectTypeAndUserId(
                accessObjectUuid = companyId,
                accessObjectType = 1,
                userId = userId,
        )?.role
    }

    fun setOwner(companyId: UUID, userId: UUID): Boolean {
        val company =
                companyReadRepository.findById(companyId).orElseThrow { throw EntityNotFoundException("$companyId company not found") }
        return if (company.data.createdBy == null) {
            company.data.createdBy = userId
            companyReadRepository.save(company)
            userPermissionRoleReadRepository.save(
                    UserPermissionRoleReadEntity(
                            userId = userId,
                            role = UserPermissionRoleReadEntity.RoleType.Owner,
                            accessObjectUuid = companyId,
                    ),
            )
            true
        } else {
            false
        }
    }

    fun setPermission(companyId: UUID, userId: UUID, roleType: UserPermissionRoleReadEntity.RoleType) {
        userPermissionRoleReadRepository.save(
                userPermissionRoleReadRepository.findByAccessObjectUuidAndAccessObjectTypeAndUserId(
                        accessObjectUuid = companyId,
                        userId = userId,
                )?.apply {
                    role = roleType
                } ?: UserPermissionRoleReadEntity(accessObjectUuid = companyId, userId = userId, role = roleType),
        )
    }
}

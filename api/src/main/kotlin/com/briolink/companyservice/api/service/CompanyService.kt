package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.companyservice.common.domain.v1_0.Keyword
import com.briolink.companyservice.common.domain.v1_0.Occupation
import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.event.v1_0.CompanyUpdatedEvent
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class CompanyService(
    private val companyReadRepository: CompanyReadRepository,
    private val companyWriteRepository: CompanyWriteRepository,
    val applicationEventPublisher: ApplicationEventPublisher,
    private val awsS3Service: AwsS3Service
) {
    fun createCompany(createCompany: CompanyWriteEntity): Company {
        val company = companyWriteRepository.save(createCompany)
        val companyDomain = Company(
                id = company.id!!,
                name = company.name,
                slug = company.slug,
                website = company.website,
        )
        applicationEventPublisher.publishEvent(
                CompanyCreatedEvent(companyDomain),
        )
        return companyDomain
    }

    fun updateCompany(company: CompanyWriteEntity): Company {
        val companyWrite = companyWriteRepository.save(company)
        val domain = Company(
                id = companyWrite.id!!,
                name = companyWrite.name,
                website = companyWrite.website,
                about = companyWrite.about,
                slug = companyWrite.slug,
                country = companyWrite.country,
                state = companyWrite.state,
                logo = companyWrite.logo.toString(),
                isTypePublic = companyWrite.isTypePublic,
                city = companyWrite.city,
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
                keywords = company.keywords.let { listKeyword ->
                    listKeyword.map {
                        Keyword(
                                it.id!!,
                                it.name
                        )
                    }
                }
        )
            applicationEventPublisher.publishEvent(CompanyUpdatedEvent(domain))
        return domain
    }

    fun deleteCompany(id: UUID) = companyWriteRepository.deleteById(id)
    fun getCompanyBySlug(slug: String): CompanyReadEntity = companyReadRepository.findBySlug(slug)
    fun findById(id: UUID): Optional<CompanyWriteEntity> = companyWriteRepository.findById(id)
    fun uploadCompanyProfileImage(id: UUID, image: MultipartFile?): URL? {
        val company = findById(id).orElseThrow { throw EntityNotFoundException("company with $id not found") }
        company.logo = image?.let {
            awsS3Service.uploadImage("/uploads/company/profile-image", image)
        }
        companyWriteRepository.saveAndFlush(company)
        return company.logo
    }
}

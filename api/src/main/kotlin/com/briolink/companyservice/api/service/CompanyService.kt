package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class CompanyService(
    private val companyReadRepository: CompanyReadRepository,
    private val companyWriteRepository: CompanyWriteRepository,
    val applicationEventPublisher: ApplicationEventPublisher
) {
    fun createCompany(inputCompany: CompanyWriteEntity) {
        val company = companyWriteRepository.save(inputCompany)
        applicationEventPublisher.publishEvent(
                CompanyCreatedEvent(
                        data = Company(
                                id = company.id.toString(),
                                name = company.name,
                                slug = company.slug,
                                website = company.website,
                        ),
                ),
        )
    }

    fun updateCompany(company: CompanyWriteEntity) = companyWriteRepository.save(company)
    fun deleteCompany(id: UUID) = companyWriteRepository.deleteById(id)
    fun getCompanyBySlug(slug: String): CompanyReadEntity = companyReadRepository.findBySlug(slug)
}

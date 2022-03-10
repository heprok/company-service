package com.briolink.companyservice.updater.handler.company

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.service.LocationService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class CompanyHandlerService(
    private val companyReadRepository: CompanyReadRepository,
    private val locationService: LocationService,
) {

    fun createOrUpdate(entityPrevCompany: CompanyReadEntity? = null, companyDomain: Company): CompanyReadEntity {
        val company = entityPrevCompany ?: CompanyReadEntity(companyDomain.id, companyDomain.slug, companyDomain.name)
        company.apply {
            name = companyDomain.name
            slug = companyDomain.slug
            data = CompanyReadEntity.Data(
                name = companyDomain.name,
                website = companyDomain.website,
                facebook = companyDomain.facebook,
                twitter = companyDomain.twitter,
                isTypePublic = companyDomain.isTypePublic,
                logo = companyDomain.logo,
                description = companyDomain.description,
                industry = companyDomain.industry?.let { CompanyReadEntity.Industry(it.id, it.name) },
                keywords = companyDomain.keywords?.let { list ->
                    list.map {
                        CompanyReadEntity.Keyword(
                            it.id,
                            it.name,
                        )
                    }
                } ?: mutableListOf(),
                occupation = companyDomain.occupation?.let { CompanyReadEntity.Occupation(it.id, it.name) },
            ).apply {
//                location = companyDomain.locationId?.let { locationService.getLocation(it) }
            }
            return companyReadRepository.save(this)
        }
    }

    fun findById(companyId: UUID): CompanyReadEntity? = companyReadRepository.findByIdOrNull(companyId)
}

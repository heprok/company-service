package com.briolink.companyservice.updater.handler.companyservice

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ProjectReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class CompanyServiceHandlerService(
    private val serviceReadRepository: ServiceReadRepository,
    private val projectReadRepository: ProjectReadRepository,
) {
    fun createOrUpdate(service: CompanyServiceEventData) {
        serviceReadRepository.findById(service.id).orElse(
            ServiceReadEntity(service.id),
        ).apply {
            companyId = service.companyId
            name = service.name
            price = service.price
            data = ServiceReadEntity.Data(
                logo = service.logo,
                slug = service.slug,
            )
            serviceReadRepository.save(this)
        }
    }

    // TODO refresh statistic by company
    fun setHidden(serviceId: UUID, hidden: Boolean) {
        serviceReadRepository.setHidden(serviceId, hidden)
        if (hidden) projectReadRepository.updateServiceSlug(serviceId.toString(), "-1")
        else projectReadRepository.updateServiceSlug(
            serviceId.toString(), serviceReadRepository.getSlugById(serviceId) ?: "-1"
        )
    }

    fun deleteById(serviceId: UUID) {
        serviceReadRepository.setDeleted(serviceId, true)
        projectReadRepository.updateServiceSlug(serviceId.toString(), "-1")
    }
}

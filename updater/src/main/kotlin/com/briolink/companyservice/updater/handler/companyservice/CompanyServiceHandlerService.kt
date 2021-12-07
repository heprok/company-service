package com.briolink.companyservice.updater.handler.companyservice

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class CompanyServiceHandlerService(
    private val serviceReadRepository: ServiceReadRepository,
) {
    fun createOrUpdate(service: CompanyService) {
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

    fun hideById(id: UUID) {
        serviceReadRepository.hideById(id)
    }

    fun deleteById(id: UUID) {
        serviceReadRepository.deleteById(id)
    }

    fun refreshVerifyUses(serviceId: UUID) {
        serviceReadRepository.refreshVerifyUses(serviceId)
    }
}

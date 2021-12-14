package com.briolink.companyservice.updater.handler.companyservice

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.common.jpa.runAfterTxCommit
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class CompanyServiceHandlerService(
    private val serviceReadRepository: ServiceReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
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

    fun setHidden(serviceId: UUID, hidden: Boolean) {
        serviceReadRepository.setHidden(serviceId, hidden)
        if (hidden) connectionReadRepository.updateServiceSlug(serviceId.toString(), "-1")
        else connectionReadRepository.updateServiceSlug(
            serviceId.toString(), serviceReadRepository.getSlugById(serviceId) ?: "-1"
        )
    }

    fun deleteById(serviceId: UUID) {
        serviceReadRepository.setDeleted(serviceId, true)
        connectionReadRepository.updateServiceSlug(serviceId.toString(), "-1")
    }

    fun refreshVerifyUses(serviceId: UUID) {
        runAfterTxCommit { serviceReadRepository.refreshVerifyUses(serviceId) }
    }
}

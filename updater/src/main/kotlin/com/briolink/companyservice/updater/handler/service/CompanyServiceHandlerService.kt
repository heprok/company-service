package com.briolink.companyservice.updater.handler.service

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.updater.dto.CompanyService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
            price = price
            verifiedUses = service.verifiedUses
            lastUsed = service.lastUsed
            data = ServiceReadEntity.Data(
                    logo = service.logo,
                    slug = service.slug
            )
            serviceReadRepository.save(this)
        }
    }
}


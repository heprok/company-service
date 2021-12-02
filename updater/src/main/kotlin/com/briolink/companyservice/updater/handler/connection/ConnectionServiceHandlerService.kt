package com.briolink.companyservice.updater.handler.connection

import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionServiceReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ConnectionServiceHandlerService(
    private val connectionServiceReadRepository: ConnectionServiceReadRepository
) {
    fun updateCompany(company: CompanyReadEntity) {
        connectionServiceReadRepository.updateCompany(
            companyId = company.id,
            slug = company.slug,
            name = company.name,
            logo = company.data.logo?.toString(),
            industryName = company.data.industry?.name,
            location = company.data.location?.toString()
        )
    }
}

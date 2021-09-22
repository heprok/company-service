package com.briolink.companyservice.api.service.company

import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CompanyQueryService(private val companyReadRepository: CompanyReadRepository) {
    fun getCompanyById(id: UUID): CompanyReadEntity = companyReadRepository.findById(id).get()
}

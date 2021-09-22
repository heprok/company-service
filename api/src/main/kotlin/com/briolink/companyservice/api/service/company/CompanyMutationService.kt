package com.briolink.companyservice.api.service.company

import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class CompanyMutationService(private val companyRepository: CompanyWriteRepository) {
    fun createCompany(company: CompanyWriteEntity): CompanyWriteEntity = companyRepository.save(company)
    fun updateCompany(company: CompanyWriteEntity) = companyRepository.save(company)
    fun deleteCompany(id: UUID) = companyRepository.findById(id)
}

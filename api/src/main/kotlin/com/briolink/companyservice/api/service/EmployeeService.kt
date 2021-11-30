package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class EmployeeService(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
) {
    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<UserJobPositionReadEntity> =
        userJobPositionReadRepository.findByCompanyId(id, PageRequest(offset, limit))
//
//    fun getEmployees(companyId: UUID, limit: Int, offset: Int) : Page<UserJobPositionReadEntity> {
//        userJobPositionReadRepository.getEmployees(companyId, PageRequest(offset, limit))
//    }
}

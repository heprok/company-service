package com.briolink.companyservice.api.service

import com.briolink.companyservice.api.service.employee.EmployeeService
import com.briolink.companyservice.common.jpa.enumeration.UserJobPositionVerifyStatusEnum
import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.EmployeeReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class UserJobPositionService(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val employeeReadRepository: EmployeeReadRepository,
    private val employeeService: EmployeeService
) {

    private fun getById(id: UUID): UserJobPositionReadEntity =
        userJobPositionReadRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("UserJobPosition with id $id not found") }

    private fun existCurrentUserJobPosition(
        companyId: UUID,
        userId: UUID
    ): Boolean = userJobPositionReadRepository.existsByCompanyIdAndUserIdAndStatusAndEndDateIsNull(
        companyId = companyId,
        userId = userId,
        status = UserJobPositionVerifyStatusEnum.Verified.value,
    )

    fun deleteById(id: UUID) {
        return deleteById(listOf(id))
    }

    fun deleteById(ids: List<UUID>) {
        val userJobPosition = getById(ids.first())
        userJobPositionReadRepository.deleteById(ids)
        if (!existCurrentUserJobPosition(userJobPosition.companyId, userJobPosition.companyId)) {
            employeeService.deleteEmployee(userJobPosition.companyId, userJobPosition.userId)
        }
    }

    fun setFormerJobPosition(id: UUID) {
        return setFormerJobPosition(listOf(id))
    }

    fun setFormerJobPosition(ids: List<UUID>) {
        val userJobPosition = getById(ids.first())

        userJobPositionReadRepository.setFormerEmployee(ids)
        if (!existCurrentUserJobPosition(userJobPosition.companyId, userJobPosition.userId)) {
            employeeService.setFormerEmployee(userJobPosition.companyId, userJobPosition.userId)
        }
    }
}

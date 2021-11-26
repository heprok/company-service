package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.companyservice.common.jpa.read.entity.EmployeeReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.EmployeeReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.handler.company.CompanyHandlerService
import com.briolink.companyservice.updater.handler.statistic.StatisticHandlerService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class UserJobPositionHandlerService(
    private val employeeReadRepository: EmployeeReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val userReadRepository: UserReadRepository,
    private val companyHandlerService: CompanyHandlerService,
    private val statisticHandlerService: StatisticHandlerService
) {
    fun createOrUpdate(userJobPosition: UserJobPosition) {
        val userReadEntity = userReadRepository.findById(userJobPosition.userId)
            .orElseThrow { throw EntityNotFoundException(userJobPosition.userId.toString() + " user not found") }
        val employeeReadEntity = employeeReadRepository.findByUserJobPositionId(userJobPositionId = userJobPosition.id).let {
            if (it == null || (it.companyId != userJobPosition.companyId && it.userId != userJobPosition.userId)) {
                it?.also { delete(userJobPosition.id) }
                companyHandlerService.addEmployee(
                    companyId = userJobPosition.companyId,
                    userId = userJobPosition.userId
                )
                connectionReadRepository.changeVisibilityByCompanyIdAndUserId(
                    companyId = userJobPosition.companyId,
                    userId = userJobPosition.userId, false,
                )
                statisticHandlerService.refreshByCompanyId(userJobPosition.companyId)
                EmployeeReadEntity(
                    companyId = userJobPosition.companyId,
                    userId = userJobPosition.userId,
                    data = EmployeeReadEntity.Data(
                        user = EmployeeReadEntity.User(
                            firstName = userReadEntity.data.firstName,
                            slug = userReadEntity.data.slug,
                            lastName = userReadEntity.data.lastName,
                            image = userReadEntity.data.image,
                        ),
                    ),
                )
            } else it
        }
        employeeReadEntity.apply {
            data.userJobPositions[userJobPosition.id] = data.userJobPositions.getOrDefault(
                userJobPosition.id,
                EmployeeReadEntity.UserJobPosition(
                    // TODO При добавлении верификации изменить
                    status = EmployeeReadEntity.VerifyStatus.Verified,
                    verifiedBy = null,
                    isCurrent = userJobPosition.isCurrent,
                    startDate = userJobPosition.startDate,
                    endDate = userJobPosition.endDate,
                    title = userJobPosition.title
                ),
            )
            if (!userJobPositionIds.contains(userJobPosition.id)) userJobPositionIds.add(userJobPosition.id)
            employeeReadRepository.save(this)
        }
    }

    fun updateUser(user: UserReadEntity) {
        employeeReadRepository.updateUserByUserId(
            userId = user.id,
            slug = user.data.slug,
            firstName = user.data.firstName,
            lastName = user.data.lastName,
            image = user.data.image?.toString(),
        )
    }

    fun delete(userJobPositionId: UUID) {
        employeeReadRepository.findByUserJobPositionId(userJobPositionId)?.apply {
            this.userJobPositionIds.remove(userJobPositionId)
            this.data.userJobPositions.remove(userJobPositionId)
            if (this.userJobPositionIds.isEmpty())
                employeeReadRepository.deleteByUserIdAndCompanyId(userId = userId, companyId = companyId)
            else
                employeeReadRepository.save(this)
        }
    }
}

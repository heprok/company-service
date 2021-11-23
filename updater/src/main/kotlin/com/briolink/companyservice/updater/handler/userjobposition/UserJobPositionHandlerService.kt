package com.briolink.companyservice.updater.handler.userjobposition

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.handler.company.CompanyHandlerService
import com.briolink.companyservice.updater.handler.statistic.StatisticHandlerService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class UserJobPositionHandlerService(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val userReadRepository: UserReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val statisticHandlerService: StatisticHandlerService,
    private val companyHandlerService: CompanyHandlerService
) {
    fun createOrUpdate(userJobPosition: UserJobPosition) {
        if (userJobPosition.endDate == null) {
            userJobPositionReadRepository.findById(userJobPosition.id).orElse(
                    UserJobPositionReadEntity(
                            id = userJobPosition.id,
                            userId = userJobPosition.userId,
                            companyId = userJobPosition.companyId,
                    ).apply {
                        userReadRepository.findById(userJobPosition.userId)
                                .orElseThrow { throw EntityNotFoundException(userJobPosition.userId.toString() + " user not found") }.let {
                                    data = UserJobPositionReadEntity.Data(
                                            user = UserJobPositionReadEntity.User(
                                                    firstName = it.data.firstName,
                                                    lastName = it.data.lastName,
                                                    slug = it.data.slug,
                                                    image = it.data.image,
                                            ),
                                    )
                                }
                        companyHandlerService.addEmployee(companyId = userJobPosition.companyId, userId = userJobPosition.userId)
                        connectionReadRepository.changeVisibilityByCompanyIdAndUserId(companyId = userJobPosition.companyId, userId = userJobPosition.userId, false)
                        statisticHandlerService.refreshByCompanyId(userJobPosition.companyId)
                    },
            ).apply {
                companyId = userJobPosition.companyId
                data.title = userJobPosition.title
                userJobPositionReadRepository.save(this)
            }
        } else {
            delete(userJobPosition.id)
        }
    }

    fun updateUser(user: UserReadEntity) {
        userJobPositionReadRepository.updateUserByUserId(
                userId = user.id,
                slug = user.data.slug,
                firstName = user.data.firstName,
                lastName = user.data.lastName,
                image = user.data.image.toString(),
        )
    }

    fun delete(id: UUID) {
        userJobPositionReadRepository.deleteById(id)
    }
}

package com.briolink.companyservice.updater.userjobposition

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.handler.company.CompanyHandlerService
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class UserJobPositionHandlerService(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val userReadRepository: UserReadRepository,
    private val companyHandlerService: CompanyHandlerService
) {
    fun createOrUpdate(userJobPosition: UserJobPosition) {
        if (userJobPosition.endDate == null) {
            val jobPosition = userJobPositionReadRepository.findById(userJobPosition.id).orElse(
                    UserJobPositionReadEntity(
                            id = userJobPosition.id,
                            userId = userJobPosition.userId,
                            companyId = userJobPosition.companyId,
                    ).apply {
                        val user = userReadRepository.findById(userJobPosition.userId)
                                .orElseThrow { throw EntityNotFoundException(userJobPosition.userId.toString() + " user not found") }
                        data = UserJobPositionReadEntity.Data(
                                user = UserJobPositionReadEntity.User(
                                        firstName = user.data.firstName,
                                        lastName = user.data.lastName,
                                        slug = user.data.slug,
                                        image = user.data.image,
                                ),
                        )
                        companyHandlerService.setPermission(
                                companyId = userJobPosition.companyId,
                                userId = userJobPosition.userId,
                                roleType = UserPermissionRoleReadEntity.RoleType.Employee,
                        )
                    },
            )
            jobPosition.companyId = userJobPosition.companyId
            jobPosition.data.title = userJobPosition.title
            userJobPositionReadRepository.save(jobPosition)
        } else {
            delete(userJobPosition.id)
        }
    }

    fun delete(id: UUID) {
        try {
            userJobPositionReadRepository.deleteById(id)
        } catch (e: EmptyResultDataAccessException) {

        }
    }
}
